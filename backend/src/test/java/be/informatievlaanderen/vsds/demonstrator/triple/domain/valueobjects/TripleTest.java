package be.informatievlaanderen.vsds.demonstrator.triple.domain.valueobjects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class TripleTest {
    private static final String SUBJECT = "my-subject";
    private static final String PREDICATE = "my-predicate";
    private static final String OBJECT = "my-object";
    private static final Triple triple = new Triple(new Node(SUBJECT), new Node(PREDICATE), new Node(OBJECT));

    @Test
    void test_equality() {
        final Triple other = new Triple(new Node(SUBJECT), new Node(PREDICATE), new Node(OBJECT));

        assertThat(triple.hashCode())
                .isEqualTo(triple.hashCode())
                .isEqualTo(other.hashCode());

        assertThat(triple)
                .isEqualTo(other)
                .isEqualTo(triple);

        assertThat(other).isEqualTo(triple);

    }

    @ParameterizedTest
    @ArgumentsSource(TripleArgumentsProvider.class)
    void test_inequality(Object other) {
        assertThat(other).isNotEqualTo(triple);
    }

    static class TripleArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(
                    new Triple(new Node("false-subject"), new Node("false-predicate"), new Node("false-object")),
                    new Triple(new Node("other-subject"), new Node(PREDICATE), new Node(OBJECT)),
                    new Triple(new Node(SUBJECT), new Node("other-predicate"), new Node(OBJECT)),
                    new Triple(new Node(SUBJECT), new Node(PREDICATE), new Node("other-object")),
                    null,
                    new Object()
            ).map(Arguments::of);
        }
    }
}