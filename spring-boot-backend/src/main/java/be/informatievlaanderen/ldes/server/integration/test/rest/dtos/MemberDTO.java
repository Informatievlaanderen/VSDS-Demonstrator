package be.informatievlaanderen.ldes.server.integration.test.rest.dtos;

import be.informatievlaanderen.ldes.server.integration.test.domain.membergeometry.entities.MemberGeometry;
import org.apache.jena.ext.com.google.common.collect.Iterables;
import org.apache.jena.geosparql.implementation.GeometryWrapper;
import org.apache.jena.geosparql.implementation.vocabulary.SRS_URI;
import org.apache.jena.rdf.model.*;
import org.locationtech.jts.geom.Geometry;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

import java.util.List;

import static org.apache.jena.rdf.model.ResourceFactory.createResource;

public class MemberDTO {
    private final Model model;

    public MemberDTO(Model model) {
        this.model = model;
    }

    public MemberGeometry getMemberGeometry(List<String> streams) throws FactoryException, TransformException {
        Geometry geometry = getMemberGeometry();
        String memberId = getMemberId(streams);
        return new MemberGeometry(memberId, geometry);
    }

    private Geometry getMemberGeometry() throws FactoryException, TransformException {
        RDFNode wktObject = Iterables.getOnlyElement(model
                .listObjectsOfProperty(model.createProperty("http://www.opengis.net/ont/geosparql#asWKT")).toList());
        GeometryWrapper geometryWrapper = ((GeometryWrapper) wktObject.asLiteral().getValue()).convertSRS(SRS_URI.WGS84_CRS);
        return geometryWrapper.getXYGeometry();
    }

    private String getMemberId(List<String> streams) {
        return Iterables.getOnlyElement(streams
                .stream()
                .map(stream -> model.listStatements((null), (null), createResource(stream)))
                .map(StmtIterator::nextStatement)
                .map(Statement::getSubject)
                .map(Resource::getURI)
                .toList());
    }
}
