package be.informatievlaanderen.vsds.demonstrator.member.application.exceptions;

public class NoGeometryProvidedException extends RuntimeException {
    @Override
    public String getMessage() {
        return "No geometry could be found in the provided member";
    }
}
