package be.informatievlaanderen.ldes.server.integration.test.application.exceptions;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFWriter;

@SuppressWarnings("java:S1948")
public class InvalidGeometryProvidedException extends RuntimeException {
    private final Model model;

    public InvalidGeometryProvidedException(Model model, Throwable cause) {
        super(cause);
        this.model = model;
    }

    @Override
    public String getMessage() {
        return "Invalid geometry provided in the following model: \n" + RDFWriter.source(model).lang(Lang.TURTLE).asString();
    }
}
