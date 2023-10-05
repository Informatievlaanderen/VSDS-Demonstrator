package be.informatievlaanderen.vsds.demonstrator.member.application.exceptions;

public class MissingCollectionException extends RuntimeException{
    private final String collectionName;

    public MissingCollectionException(String collectionName) {
        this.collectionName = collectionName;
    }

    @Override
    public String getMessage() {
        return "Eventstream with name " + collectionName + " could not be found.";
    }
}
