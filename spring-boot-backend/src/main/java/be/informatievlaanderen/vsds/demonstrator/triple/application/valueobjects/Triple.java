package be.informatievlaanderen.vsds.demonstrator.triple.application.valueobjects;

import java.util.Objects;

public class Triple {
    private final String subject;
    private final String predicate;
    private final String object;

    public Triple(String subject, String predicate, String object) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Triple triple = (Triple) o;

        if (!Objects.equals(subject, triple.subject)) return false;
        if (!Objects.equals(predicate, triple.predicate)) return false;
        return Objects.equals(object, triple.object);
    }

    @Override
    public int hashCode() {
        int result = subject != null ? subject.hashCode() : 0;
        result = 31 * result + (predicate != null ? predicate.hashCode() : 0);
        result = 31 * result + (object != null ? object.hashCode() : 0);
        return result;
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
