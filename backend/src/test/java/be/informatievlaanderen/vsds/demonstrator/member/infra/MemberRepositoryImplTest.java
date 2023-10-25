package be.informatievlaanderen.vsds.demonstrator.member.infra;

import be.informatievlaanderen.vsds.demonstrator.member.domain.member.entities.Member;
import be.informatievlaanderen.vsds.demonstrator.member.domain.member.repositories.MemberRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberRepositoryImplTest {
    private static final String ID = "member-id";
    private static final String COLLECTION = "collection";
    private static final String OTHER_COLLECTION = "otherCollection";
    private static final String IS_VERSION_OF = "http://item.be/id";
    private static final LocalDateTime timestamp = ZonedDateTime.parse("2022-05-20T09:58:15.867Z").toLocalDateTime();
    private static final LocalDateTime startTime = ZonedDateTime.parse("2022-05-20T09:53:15.867Z").toLocalDateTime();
    private static final LocalDateTime endTime = ZonedDateTime.parse("2022-05-20T10:03:15.867Z").toLocalDateTime();
    private static Geometry point;
    private static Geometry rectangle;
    private MemberRepository repository;
    @Mock
    private MemberEntityJpaRepository jpaRepository;

    @BeforeAll
    static void beforeAll() throws ParseException {
        final WKTReader reader = new WKTReader();
        point = reader.read("POINT(1 1)");
        rectangle = reader.read("POLYGON((0 0, 0 5, 5 5, 5 0, 0 0))");
    }

    @BeforeEach
    void setUp() {
        repository = new MemberRepositoryImpl(jpaRepository);
    }

    @Nested
    class RetrievalById {
        @Test
        void when_DbContainsMember_then_ReturnMemberInOptional() {
            final MemberEntity entity = new MemberEntity(ID, COLLECTION, point, timestamp, IS_VERSION_OF, Map.of());
            final Member member = new Member(ID, COLLECTION, point, IS_VERSION_OF, timestamp, Map.of());

            when(jpaRepository.findById(ID)).thenReturn(Optional.of(entity));

            Optional<Member> retrievedMemberGeometry = repository.findByMemberId(ID);

            assertTrue(retrievedMemberGeometry.isPresent());
            assertEquals(member, retrievedMemberGeometry.get());

            verify(jpaRepository).findById(ID);
        }

        @Test
        void when_DbDoesNotContainMember_then_ReturnEmptyOptional() {
            when(jpaRepository.findById(ID)).thenReturn(Optional.empty());

            Optional<Member> memberGeometry = repository.findByMemberId(ID);

            assertTrue(memberGeometry.isEmpty());
            verify(jpaRepository).findById(ID);
        }
    }

    @Nested
    class RetrievalInRectangle {
        @Test
        void when_DbDoesContainMembers_then_ReturnMembersInRectangle() throws ParseException {
            final List<MemberEntity> entities = initMembers();
            final List<Member> members = entities.stream()
                    .map(entity -> new Member(entity.getMemberId(), entity.getCollection(), entity.getGeometry(), IS_VERSION_OF, timestamp, Map.of()))
                    .toList();

            when(jpaRepository.getMemberGeometryEntitiesCoveredByGeometryInTimePeriodAndCollection(COLLECTION, startTime, endTime)).thenReturn(entities);

            assertEquals(members, repository.getMembersByGeometry(rectangle, COLLECTION, startTime, endTime));

            verify(jpaRepository).getMemberGeometryEntitiesCoveredByGeometryInTimePeriodAndCollection(COLLECTION, startTime, endTime);
        }

        @Test
        void when_DbContainsOnlyMembersOutsideRectangle_then_ReturnEmptyList() {
            when(jpaRepository.getMemberGeometryEntitiesCoveredByGeometryInTimePeriodAndCollection(COLLECTION, startTime, endTime)).thenReturn(List.of());

            assertEquals(List.of(), repository.getMembersByGeometry(rectangle, COLLECTION, startTime, endTime));

            verify(jpaRepository).getMemberGeometryEntitiesCoveredByGeometryInTimePeriodAndCollection(COLLECTION, startTime, endTime);
        }
    }

    @Test
    void test_Saving() {
        Member member = new Member(ID, COLLECTION, point, IS_VERSION_OF, timestamp, Map.of());

        repository.saveMember(member);

        verify(jpaRepository).save(argThat(entity -> entity.getMemberId().equals(member.getMemberId())));
    }

    @Test
    void test_NumberCount() {
        repository.getNumberOfMembers();

        verify(jpaRepository).count();
    }

    @Test
    void test_findMembersAfterLocalDateTime() {
        LocalDateTime now = LocalDateTime.now();
        List<MemberEntity> memberEntities = List.of(new MemberEntity(ID, COLLECTION, point, timestamp, IS_VERSION_OF, Map.of()));
        when(jpaRepository.findByCollectionAndTimestampAfter(COLLECTION, now)).thenReturn(memberEntities);
        List<Member> expectedMembers = memberEntities.stream()
                .map(entity -> new Member(entity.getMemberId(), entity.getCollection(), entity.getGeometry(), IS_VERSION_OF, entity.getTimestamp(), Map.of()))
                .toList();

        List<Member> membersAfterLocalDateTime = repository.findMembersByCollectionAfterLocalDateTime(COLLECTION, now);

        verify(jpaRepository).findByCollectionAndTimestampAfter(COLLECTION, now);
        assertEquals(1, membersAfterLocalDateTime.size());
        assertEquals(expectedMembers, membersAfterLocalDateTime);
    }

    private List<MemberEntity> initMembers() throws ParseException {
        final WKTReader reader = new WKTReader();
        List<MemberEntity> members = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                Geometry geometry = reader.read("POINT(%d %d)".formatted(i, j));
                members.add(new MemberEntity("id-%d".formatted(i * 6 + j), COLLECTION, geometry, timestamp, IS_VERSION_OF, Map.of()));
            }
        }
        return members;
    }
}