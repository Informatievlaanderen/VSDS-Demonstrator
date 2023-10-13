package be.informatievlaanderen.vsds.demonstrator.member.application.valueobjects;

import be.informatievlaanderen.vsds.demonstrator.member.application.config.EventStreamConfig;
import be.informatievlaanderen.vsds.demonstrator.member.application.exceptions.NoGeometryProvidedException;
import be.informatievlaanderen.vsds.demonstrator.member.domain.member.entities.Member;
import org.apache.jena.geosparql.implementation.GeometryWrapper;
import org.apache.jena.geosparql.implementation.vocabulary.SRS_URI;
import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.impl.SelectorImpl;
import org.locationtech.jts.geom.Geometry;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.Function;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;

public class IngestedMemberDto {
    private final String collection;
    private final Model model;

    public IngestedMemberDto(String collection, Model model) {
        this.collection = collection;
        this.model = model;
    }

    public Model getModel() {
        return model;
    }

    public Member getMember(EventStreamConfig eventStreamConfig) throws FactoryException, TransformException {
        Geometry geometry = getMember();
        String memberId = getMemberId(eventStreamConfig);
        String timestampString = (String) getTimestampPath(eventStreamConfig);
        LocalDateTime timestamp;
        if (timestampString.contains("Z") || timestampString.contains("+")) {
            timestamp = ZonedDateTime.parse(timestampString).toLocalDateTime();
        } else {
            timestamp = LocalDateTime.parse(timestampString);
        }
        return new Member(memberId, collection, geometry, timestamp);
    }

    private Geometry getMember() throws FactoryException, TransformException {
        List<RDFNode> wktNodes = model.listObjectsOfProperty(model.createProperty("http://www.opengis.net/ont/geosparql#asWKT")).toList();
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

    private String getMemberId(EventStreamConfig config) {
        return getMemberProperty(config, stream -> new SelectorImpl(null, null, createResource(stream.getMemberType())))
                .getSubject()
                .getURI();
    }

    private Object getTimestampPath(EventStreamConfig config) {
        return getMemberProperty(config, stream -> new SelectorImpl(null, createProperty(stream.getTimestampPath()), (Resource) null))
                .getObject()
                .asLiteral().getString();
    }

    public String getCollection() {
        return collection;
    }
}
