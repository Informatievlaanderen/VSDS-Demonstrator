package be.informatievlaanderen.vsds.demonstrator.triple.domain.valueobjects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

public class Node {
    private final String value;
    private Map.Entry<String, String> namespace;

    public Node(@NotNull String value) {
        this.value = value;
    }

    public Node(@NotNull String value, @Nullable Map.Entry<String, String> namespace) {
        this.value = value;
        this.namespace = namespace;
    }

    public String getValue() {
        return value;
    }

    public String getPrefixedValue() {
        if (namespace != null) {
            return value.replace(namespace.getValue(), "%s:".formatted(namespace.getKey()));
        }
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        if (!value.equals(node.value)) return false;
        return Objects.equals(namespace, node.namespace);
    }

    @Override
    public int hashCode() {
        int result = value.hashCode();
        result = 31 * result + (namespace != null ? namespace.hashCode() : 0);
        return result;
    }
}
