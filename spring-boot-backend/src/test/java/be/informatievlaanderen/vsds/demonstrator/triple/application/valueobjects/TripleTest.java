package be.informatievlaanderen.vsds.demonstrator.triple.application.valueobjects;

import org.antlr.v4.runtime.tree.Tree;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TripleTest {
    private static final String SUBJECT = "my-subject";
    private static final String PREDICATE = "my-predicate";
    private static final String OBJECT = "my-object";
    private static final Triple triple = new Triple(SUBJECT, PREDICATE, OBJECT);

    @Test
    void test_equality() {
        final Triple other = new Triple(SUBJECT, PREDICATE, OBJECT);

        assertEquals(triple.hashCode(), triple.hashCode());
        assertEquals(triple.hashCode(), other.hashCode());

        assertEquals(triple, triple);
        assertEquals(triple, other);
        assertEquals(other, triple);
    }

    @ParameterizedTest
    @ArgumentsSource(TripleArgumentsProvider.class)
    void test_inequality(Triple other) {
        assertNotEquals(triple, other);
    }

    static class TripleArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
            return Stream.of(
                    new Triple("false-subject", "false-predicate", "false-object"),
                    new Triple("other-subject", PREDICATE, OBJECT),
                    new Triple(SUBJECT, "other-predicate", OBJECT),
                    new Triple(SUBJECT, PREDICATE, "other-object")
            ).map(Arguments::of);
        }
    }
}