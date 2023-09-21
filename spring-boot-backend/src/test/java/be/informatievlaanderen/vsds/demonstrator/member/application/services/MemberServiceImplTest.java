package be.informatievlaanderen.vsds.demonstrator.member.application.services;

import be.informatievlaanderen.vsds.demonstrator.member.application.config.EventStreamConfig;
import be.informatievlaanderen.vsds.demonstrator.member.application.config.StreamsConfig;
import be.informatievlaanderen.vsds.demonstrator.member.application.exceptions.ResourceNotFoundException;
import be.informatievlaanderen.vsds.demonstrator.member.application.valueobjects.IngestedMemberDto;
import be.informatievlaanderen.vsds.demonstrator.member.application.valueobjects.MemberDto;
import be.informatievlaanderen.vsds.demonstrator.member.domain.member.entities.Member;
import be.informatievlaanderen.vsds.demonstrator.member.domain.member.repositories.MemberRepository;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
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
import org.springframework.util.ResourceUtils;
import org.wololo.jts2geojson.GeoJSONReader;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {
    private static final String TIME_PERIOD = "PT5M";
    private static final Duration duration = Duration.parse(TIME_PERIOD).dividedBy(2);
    private static final LocalDateTime timestamp = ZonedDateTime.parse("2022-05-20T09:58:15.867Z").toLocalDateTime();
    private static final LocalDateTime startTime = timestamp.minus(duration);
    private static final LocalDateTime endTime = timestamp.plus(duration);
    private static Geometry rectangle;

    @Mock
    private MemberRepository repository;

    private MemberService service;

    @BeforeAll
    static void beforeAll() throws ParseException {
        rectangle = new WKTReader().read("POLYGON((0 0, 0 5, 5 5, 5 0, 0 0))");
    }

    @BeforeEach
    void setUp() {
        EventStreamConfig eventStreamConfig = new EventStreamConfig();
        eventStreamConfig.setMemberType("https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder");
        eventStreamConfig.setTimestampPath("http://www.w3.org/ns/prov#generatedAtTime");
        StreamsConfig streams = new StreamsConfig();
        streams.setStreams(List.of(eventStreamConfig));
        service = new MemberServiceImpl(repository, streams);
    }

    @Nested
    class GetMembersInRectangle {
        @Test
        void when_GetMembersInRectangle_then_ReturnListOfMemberDtos() throws ParseException {
            final GeoJSONReader geoJSONReader = new GeoJSONReader();
            final List<Member> members = initMembers();
            when(repository.getMembersByGeometry(rectangle, startTime, endTime)).thenReturn(members);

            final List<Member> retrievedMembers = service.getMembersInRectangle(rectangle, timestamp, TIME_PERIOD).stream()
                    .map(dto -> new Member(dto.getMemberId(), geoJSONReader.read(dto.getGeojsonGeometry()), dto.getTimestamp()))
                    .toList();
            assertEquals(members, retrievedMembers);
            verify(repository).getMembersByGeometry(rectangle, startTime, endTime);
        }

        @Test
        void when_GetMembersInRectangle_then_VerifyMemberIsInRectangle() throws ParseException {
            final WKTReader wktReader = new WKTReader();
            final GeoJSONReader geoJSONReader = new GeoJSONReader();
            when(repository.getMembersByGeometry(rectangle, startTime, endTime)).thenReturn(initMembers());

            List<Geometry> retrievedMembers = service.getMembersInRectangle(rectangle, timestamp, TIME_PERIOD).stream()
                    .map(dto -> geoJSONReader.read(dto.getGeojsonGeometry()))
                    .toList();
            Geometry outsidePoint = wktReader.read("POINT(6 6)");
            Geometry insidePoint = wktReader.read("POINT(3 3)");

            assertTrue(retrievedMembers.stream().noneMatch(geometry -> geometry.equals(outsidePoint)));
            assertTrue(retrievedMembers.stream().anyMatch(geometry -> geometry.equals(insidePoint)));
        }
    }

    @Nested
    class GetMemberById {
        static final String ID = "member-id";

        @Test
        void when_MemberIsPresent_then_ReturnMember() {
            Member memberGeometry = new Member(ID, rectangle, timestamp);

            when(repository.findByMemberId(ID)).thenReturn(Optional.of(memberGeometry));

            MemberDto dto = service.getMemberById(ID);
            Geometry geometry = new GeoJSONReader().read(dto.getGeojsonGeometry());
            assertEquals(memberGeometry.getMemberId(), dto.getMemberId());
            assertEquals(memberGeometry.getGeometry(), geometry);

            verify(repository).findByMemberId(ID);
        }

        @Test
        void when_MemberIsNotPresent_then_ThrowException() {
            when(repository.findByMemberId(ID)).thenReturn(Optional.empty());

            RuntimeException e = assertThrows(ResourceNotFoundException.class, () -> service.getMemberById(ID));
            assertEquals("Resource of type Member with id %s could not be found".formatted(ID), e.getMessage());

            verify(repository).findByMemberId(ID);
        }
    }


    @Test
    void test_saveMember() throws IOException {
        final String id = "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/#ID";
        Path path = ResourceUtils.getFile("classpath:members/mobility-hindrance.nq").toPath();
        Model model = RDFParser.source(path).lang(Lang.NQUADS).toModel();
        IngestedMemberDto ingestedMemberDto = new IngestedMemberDto(model);
        service.ingestMember(ingestedMemberDto);

        verify(repository).saveMember(argThat(result -> result.getMemberId().equals(id)));
    }

    private List<Member> initMembers() throws ParseException {
        final WKTReader reader = new WKTReader();
        List<Member> members = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                Geometry geometry = reader.read("POINT(%d %d)".formatted(i, j));
                members.add(new Member("id-%d".formatted(i * 6 + j), geometry, timestamp));
            }
        }
        return members;
    }
}