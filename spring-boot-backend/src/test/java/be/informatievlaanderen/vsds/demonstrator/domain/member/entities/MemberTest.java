package be.informatievlaanderen.vsds.demonstrator.domain.member.entities;

import be.informatievlaanderen.vsds.demonstrator.member.domain.member.entities.Member;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class MemberTest {
    private static final String ID = "member-id";
    private static final LocalDateTime timestamp = ZonedDateTime.parse("2022-05-20T09:58:15.867Z").toLocalDateTime();
    private static final LocalDateTime otherTimestamp = ZonedDateTime.parse("2023-09-04T09:58:15.867Z").toLocalDateTime();
    private static Geometry geometry;
    private static Geometry otherGeometry;
    private static Member member;

    @BeforeAll
    static void beforeAll() throws ParseException {
        final WKTReader reader = new WKTReader();
        geometry = reader.read("POINT(5 5)");
        otherGeometry = reader.read("POINT(10 20)");
        member = new Member(ID, geometry, timestamp);
    }

    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(MemberGeometryArgumentsProvider.class)
    void test_inequality(String name, Object other) {
        assertNotEquals(other, member);
        if (other != null) {
            assertNotEquals(other.hashCode(), member.hashCode());
        }
    }

    @Test
    void test_equality() {
        Member other = new Member(ID, geometry, timestamp);
        Member other2 = new Member(ID, otherGeometry, timestamp);

        assertEquals(member, member);
        assertEquals(member, other);
        assertEquals(member, other2);
        assertEquals(other, member);
        assertEquals(other, other2);
    }

    static class MemberGeometryArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(
                    Arguments.of("null", null),
                    Arguments.of("Other id, same geometry, same timestamp", new Member("other", geometry, timestamp)),
                    Arguments.of("Other id, other geometry, same timestamp", new Member("not-the-same", otherGeometry, timestamp)),
                    Arguments.of("Other id, same geometry, other timestamp", new Member("no-equal-id", geometry, otherTimestamp)),
                    Arguments.of("Other id, other geometry, other timestamp", new Member("random-id-that-differs", otherGeometry, otherTimestamp)),
                    Arguments.of("Not a member geometry", "Not a member geometry")
            );
        }
    }
}