package be.informatievlaanderen.vsds.demonstrator.triple.domain.valueobjects;

import org.jetbrains.annotations.NotNull;

public class Triple {
    private final Node subject;
    private final Node predicate;
    private final Node object;

    public Triple(@NotNull Node subject, @NotNull Node predicate, @NotNull Node object) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Triple triple = (Triple) o;

        if (!subject.equals(triple.subject)) return false;
        if (!predicate.equals(triple.predicate)) return false;
        return object.equals(triple.object);
    }

    @Override
    public int hashCode() {
        int result = subject.hashCode();
        result = 31 * result + predicate.hashCode();
        result = 31 * result + object.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Triple{" +
                "subject=" + subject.getValue() +
                ", predicate=" + predicate.getValue() +
                ", object=" + object.getValue() +
                '}';
    }

    public Node getSubject() {
        return subject;
    }

    public Node getPredicate() {
        return predicate;
    }

    public Node getObject() {
        return object;
    }
}
