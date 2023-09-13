package be.informatievlaanderen.ldes.server.integration.test.application.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    private final String type;
    private final String id;
    public ResourceNotFoundException(String type, String id) {
        this.type = type;
        this.id = id;
    }

    @Override
    public String getMessage() {
        return String.format("Resource of type %s with id %s could not be found", type, id);
    }
}
