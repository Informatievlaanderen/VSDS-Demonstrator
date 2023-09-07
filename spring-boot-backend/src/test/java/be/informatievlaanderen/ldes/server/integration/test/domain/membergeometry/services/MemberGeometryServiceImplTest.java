package be.informatievlaanderen.ldes.server.integration.test.domain.membergeometry.services;

import be.informatievlaanderen.ldes.server.integration.test.domain.exceptions.ResourceNotFoundException;
import be.informatievlaanderen.ldes.server.integration.test.domain.membergeometry.entities.MemberGeometry;
import be.informatievlaanderen.ldes.server.integration.test.domain.membergeometry.repositories.MemberGeometryRepository;
import be.informatievlaanderen.ldes.server.integration.test.rest.dtos.MemberGeometryDto;
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
import org.wololo.jts2geojson.GeoJSONReader;
import org.wololo.jts2geojson.GeoJSONWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberGeometryServiceImplTest {
    private static Geometry rectangle;

    @Mock
    private MemberGeometryRepository repository;

    private MemberGeometryService service;

    @BeforeAll
    static void beforeAll() throws ParseException {
        rectangle = new WKTReader().read("POLYGON((0 0, 0 5, 5 5, 5 0, 0 0))");
    }

    @BeforeEach
    void setUp() {
        service = new MemberGeometryServiceImpl(repository);
    }

    @Nested
    class GetMembersInRectangle {
        @Test
        void when_GetMembersInRectangle_then_ReturnListOfMemberGeometryDtos() throws ParseException {
            List<MemberGeometry> members = initMembers();
            when(repository.getMembersByGeometry(rectangle)).thenReturn(members);

            List<MemberGeometryDto> retrievedMembers = service.getMembersInRectangle(new GeoJSONWriter().write(rectangle));
            for (int i = 0; i < members.size(); i++) {
                MemberGeometryDto memberGeometryDto = retrievedMembers.get(i);
                MemberGeometry memberGeometry = members.get(i);
                Geometry geometry = new GeoJSONReader().read(memberGeometryDto.getGeojsonGeometry());

                assertEquals(memberGeometry.getMemberId(), memberGeometryDto.getMemberId());
                assertEquals(memberGeometry.getGeometry(), geometry);
            }
            verify(repository).getMembersByGeometry(rectangle);
        }

        @Test
        void when_GetMembersInRectangle_then_VerifyMemberGeometryIsInRectangle() throws ParseException {
            final WKTReader wktReader = new WKTReader();
            final GeoJSONReader geoJSONReader = new GeoJSONReader();
            when(repository.getMembersByGeometry(rectangle)).thenReturn(initMembers());

            List<Geometry> retrievedMembers = service.getMembersInRectangle(new GeoJSONWriter().write(rectangle)).stream()
                    .map(dto -> geoJSONReader.read(dto.getGeojsonGeometry()))
                    .toList();
            Geometry outsidePoint = wktReader.read("POINT(6 6)");
            Geometry insidePoint = wktReader.read("POINT(3 3)");

            assertTrue(retrievedMembers.stream().noneMatch(geometry -> geometry.equals(outsidePoint)));
            assertTrue(retrievedMembers.stream().anyMatch(geometry -> geometry.equals(insidePoint)));
        }
    }

    @Nested
    class GetMemberGeometryById {
        static final String ID = "member-id";

        @Test
        void when_MemberIsPresent_then_ReturnMemberGeometry() {
            MemberGeometry memberGeometry = new MemberGeometry(ID, rectangle);

            when(repository.findByMemberId(ID)).thenReturn(Optional.of(memberGeometry));

            MemberGeometryDto dto = service.getMemberById(ID);
            Geometry geometry = new GeoJSONReader().read(dto.getGeojsonGeometry());
            assertEquals(memberGeometry.getMemberId(), dto.getMemberId());
            assertEquals(memberGeometry.getGeometry(), geometry);

            verify(repository).findByMemberId(ID);
        }

        @Test
        void when_MemberIsNotPresent_then_ThrowException() {
            when(repository.findByMemberId(ID)).thenReturn(Optional.empty());

            RuntimeException e = assertThrows(ResourceNotFoundException.class, () -> service.getMemberById(ID));
            assertEquals("Resource of type MemberGeometry with id %s could not be found".formatted(ID), e.getMessage());

            verify(repository).findByMemberId(ID);
        }
    }


    @Test
    void test_saveMember() {
        MemberGeometry memberGeometry = new MemberGeometry("member-id", rectangle);
        service.ingestMemberGeometry(memberGeometry);

        verify(repository).saveMember(memberGeometry);
    }

    private List<MemberGeometry> initMembers() throws ParseException {
        List<MemberGeometry> members = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                Geometry geometry = new WKTReader().read("POINT(%d %d)".formatted(i, j));
                members.add(new MemberGeometry("id-%d".formatted(i * 6 + j), geometry));
            }
        }
        return members;
    }
}