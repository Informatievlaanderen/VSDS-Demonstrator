package be.informatievlaanderen.vsds.demonstrator.member.infra;

import be.informatievlaanderen.vsds.demonstrator.member.domain.member.entities.Member;
import be.informatievlaanderen.vsds.demonstrator.member.domain.member.repositories.MemberRepository;
import be.informatievlaanderen.vsds.demonstrator.member.infra.MemberEntity;
import be.informatievlaanderen.vsds.demonstrator.member.infra.MemberEntityJpaRepository;
import be.informatievlaanderen.vsds.demonstrator.member.infra.MemberRepositoryImpl;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberRepositoryImplTest {
    private static final String ID = "member-id";
    private static final LocalDateTime timestamp = ZonedDateTime.parse("2022-05-20T09:58:15.867Z").toLocalDateTime();
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
            final MemberEntity entity = new MemberEntity(ID, point, timestamp);
            final Member member = new Member(ID, point, timestamp);

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
                    .map(entity -> new Member(entity.getMemberId(), entity.getGeometry(), timestamp))
                    .toList();

            when(jpaRepository.getMemberGeometryEntitiesCoveredByGeometry(rectangle, timestamp)).thenReturn(entities);

            assertEquals(members, repository.getMembersByGeometry(rectangle, timestamp));

            verify(jpaRepository).getMemberGeometryEntitiesCoveredByGeometry(rectangle, timestamp);
        }

        @Test
        void when_DbContainsOnlyMembersOutsideRectangle_then_ReturnEmptyList() {
            when(jpaRepository.getMemberGeometryEntitiesCoveredByGeometry(rectangle, timestamp)).thenReturn(List.of());

            assertEquals(List.of(), repository.getMembersByGeometry(rectangle, timestamp));

            verify(jpaRepository).getMemberGeometryEntitiesCoveredByGeometry(rectangle, timestamp);
        }
    }

    @Test
    void test_Saving() {
        Member member = new Member(ID, point, timestamp);

        repository.saveMember(member);

        verify(jpaRepository).save(argThat(entity -> entity.getMemberId().equals(member.getMemberId())));
    }

    private List<MemberEntity> initMembers() throws ParseException {
        final WKTReader reader = new WKTReader();
        List<MemberEntity> members = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                Geometry geometry = reader.read("POINT(%d %d)".formatted(i, j));
                members.add(new MemberEntity("id-%d".formatted(i * 6 + j), geometry, timestamp));
            }
        }
        return members;
    }
}