package be.informatievlaanderen.vsds.demonstrator.triple.domain.valueobjects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class NodeTest {
    private static final String VALUE = "my-node-value";
    private static final String NS_PREFIX = "ns";
    private static final String NS_NAME = "some/namespace#";
    private static final Node NODE = new Node(VALUE, Map.entry(NS_PREFIX, NS_NAME));

    @Test
    void test_equality() {
        final Node other = new Node(VALUE, Map.entry(NS_PREFIX, NS_NAME));

        assertThat(other.hashCode())
                .isEqualTo(NODE.hashCode())
                .isEqualTo(other.hashCode());

        assertThat(NODE)
                .isEqualTo(other)
                .isEqualTo(NODE);

        assertThat(other).isEqualTo(NODE);
    }

    @ParameterizedTest
    @ArgumentsSource(NodeArgumentsProvider.class)
    void test_inequality(Object other) {
        assertThat(other).isNotEqualTo(NODE);
    }

    static class NodeArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(
                    new Node(VALUE, null),
                    new Node("other-value", Map.entry(NS_PREFIX, NS_NAME)),
                    new Node("other-value", null),
                    new Node("fake-value", Map.entry("other-ns", "my-custom.domain/ns#")),
                    null,
                    "This is not a node"
            ).map(Arguments::of);
        }
    }
}