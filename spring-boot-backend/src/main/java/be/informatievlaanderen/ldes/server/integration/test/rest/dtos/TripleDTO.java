package be.informatievlaanderen.ldes.server.integration.test.rest.dtos;

public class TripleDTO {
    private final String subject;
    private final String predicate;
    private final String object;

    public TripleDTO(String subject, String predicate, String object) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
    }

    public String getSubject() {
        return subject;
    }

    public String getPredicate() {
        return predicate;
    }

    public String getObject() {
        return object;
    }
}
