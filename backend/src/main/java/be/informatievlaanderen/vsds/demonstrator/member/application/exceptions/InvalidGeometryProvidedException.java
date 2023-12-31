package be.informatievlaanderen.vsds.demonstrator.member.application.exceptions;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFWriter;

public class InvalidGeometryProvidedException extends RuntimeException {
    private final transient Model model;

    public InvalidGeometryProvidedException(Model model, Throwable cause) {
        super(cause);
        this.model = model;
    }

    @Override
    public String getMessage() {
        return "Invalid geometry provided in the following model: \n" + RDFWriter.source(model).lang(Lang.TURTLE).asString();
    }
}
