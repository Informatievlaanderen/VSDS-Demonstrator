package be.informatievlaanderen.vsds.demonstrator.member.application.services;

import be.informatievlaanderen.vsds.demonstrator.member.application.config.EventStreamConfig;
import be.informatievlaanderen.vsds.demonstrator.member.application.config.StreamsConfig;
import be.informatievlaanderen.vsds.demonstrator.member.application.exceptions.InvalidGeometryProvidedException;
import be.informatievlaanderen.vsds.demonstrator.member.application.exceptions.ResourceNotFoundException;
import be.informatievlaanderen.vsds.demonstrator.member.application.valueobjects.IngestedMemberDto;
import be.informatievlaanderen.vsds.demonstrator.member.application.valueobjects.LineChartDto;
import be.informatievlaanderen.vsds.demonstrator.member.application.valueobjects.MemberDto;
import be.informatievlaanderen.vsds.demonstrator.member.application.valueobjects.MemberIngestedEvent;
import be.informatievlaanderen.vsds.demonstrator.member.domain.member.entities.Member;
import be.informatievlaanderen.vsds.demonstrator.member.domain.member.repositories.MemberRepository;
import be.informatievlaanderen.vsds.demonstrator.member.presentation.dtos.DataSetDto;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ResourceFactory;
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
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.ResourceUtils;
import org.wololo.jts2geojson.GeoJSONReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {
    private static final String COLLECTION = "gipod";
    private static final String IS_VERSION_OF = "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464";
    private static final LocalDateTime timestamp = ZonedDateTime.parse("2022-05-20T09:58:15.867Z").toLocalDateTime();
    private static Geometry rectangle;

    @Mock
    private MemberRepository repository;
    @Mock
    private ApplicationEventPublisher eventPublisher;

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
        eventStreamConfig.setVersionOfPath("http://purl.org/dc/terms/isVersionOf");
        StreamsConfig streams = new StreamsConfig();
        streams.setStreams(Map.of(COLLECTION, eventStreamConfig));
        service = new MemberServiceImpl(repository, streams, eventPublisher);
    }

    @Nested
    class GetMembersInRectangle {
        @Test
        void when_GetMembersInRectangle_then_ReturnListOfMemberDtos() throws ParseException {
            final GeoJSONReader geoJSONReader = new GeoJSONReader();
            final List<Member> members = initMembers();
            when(repository.getMembersByGeometry(eq(rectangle), eq(COLLECTION), any(), eq(timestamp))).thenReturn(members);

            final List<Member> retrievedMembers = service.getMembersInRectangle(rectangle, COLLECTION, timestamp).stream()
                    .map(dto -> new Member(dto.getMemberId(), COLLECTION, geoJSONReader.read(dto.getGeojsonGeometry()), dto.getIsVersionOf(), dto.getTimestamp(), Map.of()))
                    .toList();

            assertThat(retrievedMembers)
                    .hasSize(2)
                    .map(member -> Map.entry(member.getMemberId(), member.getIsVersionOf()))
                    .containsExactlyInAnyOrder(
                            Map.entry("http://gipod.be/stations/2/4", "http://gipod.be/stations/2"),
                            Map.entry("http://gipod.be/stations/1/5", "http://gipod.be/stations/1")
                    );

        }

        @Test
        void when_GetMembersInRectangle_then_VerifyMemberIsInRectangle() throws ParseException {
            final WKTReader wktReader = new WKTReader();
            final GeoJSONReader geoJSONReader = new GeoJSONReader();
            when(repository.getMembersByGeometry(eq(rectangle), eq(COLLECTION), any(), eq(timestamp))).thenReturn(initMembers());

            List<Geometry> retrievedMembers = service.getMembersInRectangle(rectangle, COLLECTION, timestamp).stream()
                    .map(dto -> geoJSONReader.read(dto.getGeojsonGeometry()))
                    .toList();
            Geometry outsidePoint = wktReader.read("POINT(6 6)");
            Geometry insidePoint = wktReader.read("POINT(2 4)");

            assertThat(retrievedMembers)
                    .noneMatch(geometry -> geometry.equals(outsidePoint))
                    .anyMatch(geometry -> geometry.equals(insidePoint));
        }
    }

    @Nested
    class GetMemberById {
        static final String ID = "member-id";

        @Test
        void when_MemberIsPresent_then_ReturnMember() {
            Member memberGeometry = new Member(ID, COLLECTION, rectangle, IS_VERSION_OF, timestamp, Map.of());

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

    @Nested
    class IngestMember {
        @Test
        void given_TooOldMember_when_IngestMember_then_IgnoreMember() throws IOException {
            Path path = ResourceUtils.getFile("classpath:members/mobility-hindrance.nq").toPath();
            Model model = RDFParser.source(path).lang(Lang.NQUADS).toModel();
            IngestedMemberDto ingestedMemberDto = new IngestedMemberDto(COLLECTION, model);
            service.ingestMember(ingestedMemberDto);

            verifyNoInteractions(repository, eventPublisher);
        }

        @Test
        void given_ValidMember_when_IngestMember_then_SaveMember() throws IOException {
            // TODO: replace timestamp with a timestamp that is less then seven days ago
            final String id = "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/#ID";
            Path path = ResourceUtils.getFile("classpath:members/mobility-hindrance.nq").toPath();

            Model model = RDFParser.source(path).lang(Lang.NQUADS).toModel();
            IngestedMemberDto ingestedMemberDto = new IngestedMemberDto(COLLECTION, model);
            service.ingestMember(ingestedMemberDto);

            verify(repository).saveMember(argThat(result -> result.getMemberId().equals(id)));
            verify(eventPublisher).publishEvent(argThat((MemberIngestedEvent event) -> event.getMemberDto().getMemberId().equals(id)));
        }

        @Test
        void when_IngestInvalidMember_then_ThrowException() throws FactoryException, TransformException {
            IngestedMemberDto ingestedMemberDto = mock(IngestedMemberDto.class);
            when(ingestedMemberDto.getCollection()).thenReturn(COLLECTION);
            when(ingestedMemberDto.getMember(any())).thenThrow(FactoryException.class);

            assertThatThrownBy(() -> service.ingestMember(ingestedMemberDto))
                    .isInstanceOf(InvalidGeometryProvidedException.class);
            verifyNoInteractions(eventPublisher, repository);
        }
    }


    @Test
    void test_getNumberOfMembers() {
        service.getNumberOfMembers();

        verify(repository).getNumberOfMembers();
    }


    @Test
    void test_getLineChartDto() {
        final List<Member> members = getMemberList();
        final int numberOfLabelsPerHourForOneWeek = 7 * 24 + 1;
        when(repository.findMembersByCollectionAfterLocalDateTime(eq(COLLECTION), any())).thenReturn(members);
        when(repository.getNumberOfMembersByCollection(COLLECTION)).thenReturn(Long.valueOf(members.size()));

        LineChartDto lineChartDto = service.getLineChartDtos();
        DataSetDto dataSetDto = lineChartDto.getDataSetDtos().get(0);

        verify(repository).getNumberOfMembersByCollection(COLLECTION);
        verify(repository).findMembersByCollectionAfterLocalDateTime(eq(COLLECTION), any());
        assertThat(lineChartDto.getLabels()).hasSize(numberOfLabelsPerHourForOneWeek);
        assertThat(dataSetDto.getName()).isEqualTo(COLLECTION);
        assertThat(dataSetDto.getValues()).hasSize(numberOfLabelsPerHourForOneWeek);
    }

    private List<Member> getMemberList() {
        return List.of(
                new Member("id1", COLLECTION, null, IS_VERSION_OF, LocalDateTime.now(), Map.of()),
                new Member("id1", COLLECTION, null, IS_VERSION_OF, LocalDateTime.now(), Map.of()),
                new Member("id1", COLLECTION, null, IS_VERSION_OF, LocalDateTime.now(), Map.of()),
                new Member("id1", COLLECTION, null, IS_VERSION_OF, LocalDateTime.now(), Map.of()),
                new Member("id1", COLLECTION, null, IS_VERSION_OF, LocalDateTime.now(), Map.of()),
                new Member("id1", COLLECTION, null, IS_VERSION_OF, LocalDateTime.now(), Map.of()),
                new Member("id1", COLLECTION, null, IS_VERSION_OF, LocalDateTime.now(), Map.of()),
                new Member("id1", COLLECTION, null, IS_VERSION_OF, LocalDateTime.now(), Map.of())
        );
    }

    private List<Member> initMembers() throws ParseException {
        final WKTReader reader = new WKTReader();
        int index = 0;
        List<Member> members = new ArrayList<>();

        final Map<String, String> stations = Map.of(
                "http://gipod.be/stations/1", "POINT(1 2)",
                "http://gipod.be/stations/2", "POINT(2 4)"
        );
        final List<String> stationMemberIds = List.of(
                "http://gipod.be/stations/1/0",
                "http://gipod.be/stations/2/1",
                "http://gipod.be/stations/1/2",
                "http://gipod.be/stations/1/3",
                "http://gipod.be/stations/2/4",
                "http://gipod.be/stations/1/5"
        );

        for (String memberId : stationMemberIds) {
            String isVersionOf = memberId.substring(0, 26);
            Geometry geometry = reader.read(stations.get(isVersionOf));
            Member member = new Member(memberId, COLLECTION, geometry, isVersionOf, timestamp.plusDays(index), Map.of());
            members.add(member);
            index++;
        }

        return members;
    }
}