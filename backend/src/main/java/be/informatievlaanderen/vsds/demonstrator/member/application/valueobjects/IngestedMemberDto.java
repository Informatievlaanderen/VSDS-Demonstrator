package be.informatievlaanderen.vsds.demonstrator.member.application.valueobjects;

import be.informatievlaanderen.vsds.demonstrator.member.application.config.EventStreamConfig;
import be.informatievlaanderen.vsds.demonstrator.member.application.exceptions.NoGeometryProvidedException;
import be.informatievlaanderen.vsds.demonstrator.member.application.services.PropertyPathExtractor;
import be.informatievlaanderen.vsds.demonstrator.member.domain.member.entities.Member;
import org.apache.jena.geosparql.implementation.GeometryWrapper;
import org.apache.jena.geosparql.implementation.vocabulary.SRS_URI;
import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.impl.SelectorImpl;
import org.locationtech.jts.geom.Geometry;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;

public class IngestedMemberDto {
    private static final Property RDF_SYNTAX_TYPE = createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
    private final String collection;
    private final Model model;

    public IngestedMemberDto(String collection, Model model) {
        this.collection = collection;
        this.model = model;
    }

    public String getCollection() {
        return collection;
    }

    public Model getModel() {
        return model;
    }

    public Member getMember(EventStreamConfig eventStreamConfig) throws FactoryException, TransformException {
        Geometry geometry = getGeometry(eventStreamConfig);
        String memberId = getMemberId(eventStreamConfig);
        String isVersionOf = getIsVersionOf(eventStreamConfig);
        String timestampString = getTimestamp(eventStreamConfig);
        Map<String, String> properties = getProperties(eventStreamConfig);
        LocalDateTime timestamp = getTimestamp(eventStreamConfig.getTimezoneId(), timestampString);
        return new Member(memberId, collection, geometry, isVersionOf, timestamp, properties);
    }

    private LocalDateTime getTimestamp(ZoneId timezoneId, String timestampString) {
        LocalDateTime timestamp;
        if (timestampString.contains("Z") || timestampString.contains("+")) {
            timestamp = ZonedDateTime.parse(timestampString).toLocalDateTime();
        } else {
            timestamp = LocalDateTime.parse(timestampString)
                    .atZone(timezoneId)
                    .withZoneSameInstant(ZoneOffset.UTC)
                    .toLocalDateTime();
        }
        return timestamp;
    }

    private Geometry getGeometry(EventStreamConfig config) throws FactoryException, TransformException {
        List<RDFNode> wktNodes = new ArrayList<>();
        if(config.getGeoLocationPath() != null) {
            wktNodes.addAll(PropertyPathExtractor.from(config.getGeoLocationPath()).getProperties(model));
        }
        else {
            wktNodes.addAll(model.listObjectsOfProperty(model.createProperty("http://www.opengis.net/ont/geosparql#asWKT")).toList());
        }
        if (wktNodes.isEmpty()) {
            throw new NoGeometryProvidedException();
        } else {
            RDFNode wktObject = wktNodes.get(0);
            GeometryWrapper geometryWrapper = ((GeometryWrapper) wktObject.asLiteral().getValue()).convertSRS(SRS_URI.WGS84_CRS);
            return geometryWrapper.getXYGeometry();
        }
    }

    private Statement getMemberProperty(EventStreamConfig config, Function<EventStreamConfig, Selector> createSelector) {
        return model.listStatements(createSelector.apply(config)).nextStatement();
    }

    private String getMemberId(EventStreamConfig streamConfig) {
        return getMemberProperty(streamConfig, stream -> new SelectorImpl(null, RDF_SYNTAX_TYPE, createResource(stream.getMemberType())))
                .getSubject()
                .getURI();
    }

    private String getTimestamp(EventStreamConfig streamConfig) {
        return getMemberProperty(streamConfig, stream -> new SelectorImpl(null, createProperty(stream.getTimestampPath()), (Resource) null))
                .getObject()
                .asLiteral().getString();
    }

    private String getIsVersionOf(EventStreamConfig streamConfig) {
        return getMemberProperty(streamConfig, stream -> new SelectorImpl(null, createProperty(stream.getVersionOfPath()), (Resource) null))
                .getObject()
                .asResource()
                .getURI();
    }

    private Map<String, String> getProperties(EventStreamConfig streamConfig) {
        final Map<String, String> properties = new HashMap<>();
        for (Map.Entry<String, String> propertyPredicateEntry : streamConfig.getPropertyPredicates().entrySet()) {
            model.listStatements(null, createProperty(propertyPredicateEntry.getValue()), (Resource) null)
                    .nextOptional()
                    .ifPresent(statement -> properties.put(propertyPredicateEntry.getKey(), statement.getLiteral().getString()));
        }
        return properties;
    }
}
