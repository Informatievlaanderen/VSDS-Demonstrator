package be.informatievlaanderen.ldes.server.integration.test.rest.dtos;

import be.informatievlaanderen.ldes.server.integration.test.domain.membergeometry.entities.MemberGeometry;
import be.informatievlaanderen.ldes.server.integration.test.rest.config.EventStreamConfig;
import org.apache.jena.ext.com.google.common.collect.Iterables;
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

public class MemberDTO {
    private final Model model;

    public MemberDTO(Model model) {
        this.model = model;
    }

    public MemberGeometry getMemberGeometry(List<EventStreamConfig> streams) throws FactoryException, TransformException {
        Geometry geometry = getMemberGeometry();
        String memberId = getMemberId(streams);
        String timestampString = (String) getTimestampPath(streams);
        LocalDateTime timestamp;
        if(timestampString.contains("Z")) {
            timestamp = ZonedDateTime.parse(timestampString).toLocalDateTime();
        } else {
            timestamp = LocalDateTime.parse(timestampString);
        }
        return new MemberGeometry(memberId, geometry, timestamp);
    }

    private Geometry getMemberGeometry() throws FactoryException, TransformException {
        RDFNode wktObject = Iterables.getOnlyElement(model
                .listObjectsOfProperty(model.createProperty("http://www.opengis.net/ont/geosparql#asWKT")).toList());
        GeometryWrapper geometryWrapper = ((GeometryWrapper) wktObject.asLiteral().getValue()).convertSRS(SRS_URI.WGS84_CRS);
        return geometryWrapper.getXYGeometry();
    }

    private Statement getMemberProperty(List<EventStreamConfig> streams, Function<EventStreamConfig, Selector> createSelector) {
        return Iterables.getOnlyElement(streams
                .stream()
                .map(stream -> model.listStatements(createSelector.apply(stream)))
                .map(StmtIterator::nextStatement)
                .toList());
    }

    private String getMemberId(List<EventStreamConfig> streams) {
        return getMemberProperty(streams, stream -> new SelectorImpl(null, null, createResource(stream.getMemberType())))
                .getSubject()
                .getURI();
    }

    private Object getTimestampPath(List<EventStreamConfig> streams) {
        return getMemberProperty(streams, stream -> new SelectorImpl(null, createProperty(stream.getTimestampPath()), (Resource) null))
                .getObject()
                .asLiteral().getString();

    }
}
