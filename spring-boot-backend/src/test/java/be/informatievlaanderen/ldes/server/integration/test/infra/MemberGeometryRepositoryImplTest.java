package be.informatievlaanderen.ldes.server.integration.test.infra;

import be.informatievlaanderen.ldes.server.integration.test.domain.membergeometry.entities.MemberGeometry;
import be.informatievlaanderen.ldes.server.integration.test.domain.membergeometry.repositories.MemberGeometryRepository;
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

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberGeometryRepositoryImplTest {
    private static final String ID = "member-id";
    private static Geometry point;
    private static Geometry rectangle;
    private MemberGeometryRepository repository;
    @Mock
    private MemberGeometryJpaRepository jpaRepository;

    @BeforeAll
    static void beforeAll() throws ParseException {
        final WKTReader reader = new WKTReader();
        point = reader.read("POINT(1 1)");
        rectangle = reader.read("POLYGON((0 0, 0 5, 5 5, 5 0, 0 0))");
    }

    @BeforeEach
    void setUp() {
        repository = new MemberGeometryRepositoryImpl(jpaRepository);
    }

    @Nested
    class RetrievalById {
        @Test
        void when_DbContainsMember_then_ReturnMemberInOptional() {
            final MemberGeometryEntity entity = new MemberGeometryEntity(ID, point);
            final MemberGeometry memberGeometry = new MemberGeometry(ID, point);

            when(jpaRepository.findById(ID)).thenReturn(Optional.of(entity));

            Optional<MemberGeometry> retrievedMemberGeometry = repository.findByMemberId(ID);

            assertTrue(retrievedMemberGeometry.isPresent());
            assertEquals(memberGeometry, retrievedMemberGeometry.get());

            verify(jpaRepository).findById(ID);
        }

        @Test
        void when_DbDoesNotContainMember_then_ReturnEmptyOptional() {
            when(jpaRepository.findById(ID)).thenReturn(Optional.empty());

            Optional<MemberGeometry> memberGeometry = repository.findByMemberId(ID);

            assertTrue(memberGeometry.isEmpty());
            verify(jpaRepository).findById(ID);
        }
    }

    @Nested
    class RetrievalInRectangle {
        @Test
        void when_DbDoesContainMembers_then_ReturnMembersInRectangle() throws ParseException {
            final List<MemberGeometryEntity> entities = initMembers();
            final List<MemberGeometry> members = entities.stream()
                    .map(entity -> new MemberGeometry(entity.getMemberId(), entity.getGeometry()))
                    .toList();

            when(jpaRepository.getMemberGeometryEntitiesCoveredByGeometry(rectangle)).thenReturn(entities);

            assertEquals(members, repository.getMembersByGeometry(rectangle));

            verify(jpaRepository).getMemberGeometryEntitiesCoveredByGeometry(rectangle);
        }

        @Test
        void when_DbContainsOnlyMembersOutsideRectangle_then_ReturnEmptyList() {
            when(jpaRepository.getMemberGeometryEntitiesCoveredByGeometry(rectangle)).thenReturn(List.of());

            assertEquals(List.of(), repository.getMembersByGeometry(rectangle));

            verify(jpaRepository).getMemberGeometryEntitiesCoveredByGeometry(rectangle);
        }
    }

    @Test
    void test_Saving() {
        MemberGeometry memberGeometry = new MemberGeometry(ID, point);

        repository.saveMember(memberGeometry);

        verify(jpaRepository).save(argThat(entity -> entity.getMemberId().equals(memberGeometry.getMemberId())));
    }

    private List<MemberGeometryEntity> initMembers() throws ParseException {
        final WKTReader reader = new WKTReader();
        List<MemberGeometryEntity> members = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                Geometry geometry = reader.read("POINT(%d %d)".formatted(i, j));
                members.add(new MemberGeometryEntity("id-%d".formatted(i * 6 + j), geometry));
            }
        }
        return members;
    }
}