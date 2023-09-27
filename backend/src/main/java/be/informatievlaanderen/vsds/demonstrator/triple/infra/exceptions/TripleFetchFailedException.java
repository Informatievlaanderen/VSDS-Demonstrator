package be.informatievlaanderen.vsds.demonstrator.triple.infra.exceptions;

public class TripleFetchFailedException extends RuntimeException {
    private final String id;

    public TripleFetchFailedException(String id, Throwable cause) {
        super(cause);
        this.id = id;
    }

    @Override
    public String getMessage() {
        return "Something went wrong while trying to fetch the triples with id " + id;
    }
}
