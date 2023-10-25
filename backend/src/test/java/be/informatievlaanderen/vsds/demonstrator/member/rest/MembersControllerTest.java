package be.informatievlaanderen.vsds.demonstrator.member.rest;


import be.informatievlaanderen.vsds.demonstrator.member.application.config.StreamsConfig;
import be.informatievlaanderen.vsds.demonstrator.member.application.exceptions.ResourceNotFoundException;
import be.informatievlaanderen.vsds.demonstrator.member.application.services.MemberService;
import be.informatievlaanderen.vsds.demonstrator.member.application.services.MemberValidatorImpl;
import be.informatievlaanderen.vsds.demonstrator.member.application.valueobjects.MemberDto;
import be.informatievlaanderen.vsds.demonstrator.member.custom.MeetPuntRepository;
import be.informatievlaanderen.vsds.demonstrator.member.rest.converters.ModelHttpConverter;
import be.informatievlaanderen.vsds.demonstrator.triple.infra.GraphDBConfig;
import org.apache.jena.riot.Lang;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.ResourceUtils;
import org.wololo.jts2geojson.GeoJSONWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles({"test"})
@WebMvcTest
@ContextConfiguration(classes = {MembersController.class, ModelHttpConverter.class, MemberExceptionHandler.class, GraphDBConfig.class, StreamsConfig.class, MemberValidatorImpl.class, MeetPuntRepository.class})
class MembersControllerTest {
    private static final String ID = "member-id";
    private static final String COLLECTION = "gipod";
    private static final String IS_VERSION_OF = "isVersionOf";
    private static final LocalDateTime timestamp = ZonedDateTime.parse("2022-05-20T09:58:15.867Z").toLocalDateTime();
    private static org.wololo.geojson.Geometry geoJSON;
    @MockBean
    private MemberService service;
    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    static void beforeAll() throws ParseException {
        Geometry geometry = new WKTReader().read("POINT(5 5)");
        geoJSON = new GeoJSONWriter().write(geometry);
    }

    @Nested
    class GetSingleMember {
        @Test
        void when_MemberGeometryPresent_then_MemberGeomtryIsReturned_and_StatusIs200() throws Exception {
            final String json = transformToJson(ID, geoJSON, timestamp);
            MemberDto dto = new MemberDto(ID, geoJSON, COLLECTION, timestamp, IS_VERSION_OF, Map.of());

            when(service.getMemberById(ID)).thenReturn(dto);

            mockMvc.perform(get("/api/geometry/" + ID).accept(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(json))
                    .andExpect(status().isOk());

            verify(service).getMemberById(ID);
        }

        @Test
        void when_MemberGeometryIsNotPresent_then_StatusIs404() throws Exception {
            when(service.getMemberById(ID)).thenThrow(ResourceNotFoundException.class);

            mockMvc.perform(get("/api/geometry/" + ID).accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(service).getMemberById(ID);
        }
    }

    @Test
    void when_MapBoundariesArePosted_then_ReturnMemberGeometries() throws Exception {
        final String timePeriod = "PT1M";
        Geometry rectangle = new WKTReader().read("POLYGON((0 0, 0 5, 5 5, 5 0, 0 0))");
        List<MemberDto> members = initMembers();
        String json = "[" + members.stream()
                .map(dto -> transformToJson(dto.getMemberId(), dto.getGeojsonGeometry(), dto.getTimestamp()))
                .collect(Collectors.joining(", ")) + "]";

        when(service.getMembersInRectangle(rectangle, COLLECTION, timestamp)).thenReturn(members);

        mockMvc.perform(post("/api/" + COLLECTION + "/in-rectangle")
                        .param("timestamp", timestamp.toString())
                        .param("timePeriod", timePeriod)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(readDataFromFile("mapbounds/rectangle.json")))
                .andExpect(content().json(json))
                .andExpect(status().isOk());

        verify(service).getMembersInRectangle(rectangle, COLLECTION, timestamp);
    }

    @Nested
    class IngestMember {
        @Test
        void when_MemberWithInvalidGeometryIsPosted_then_Status400IsExpected() throws Exception {
            mockMvc.perform(post("/api/" + COLLECTION + "/members")
                            .content(readDataFromFile("members/mobility-hindrance-with-invalid-wkt.nq"))
                            .contentType(Lang.NQUADS.getHeaderString()))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void when_MemberIsPosted_then_IngestMemberInService() throws Exception {
            mockMvc.perform(post("/api/" + COLLECTION + "/members")
                            .content(readDataFromFile("members/mobility-hindrance.nq"))
                            .contentType(Lang.NQUADS.getHeaderString()))
                    .andExpect(status().isOk());

            verify(service).ingestMember(any());
        }
    }

    private byte[] readDataFromFile(String filename) throws IOException {
        Path path = ResourceUtils.getFile("classpath:" + filename).toPath();
        return Files.readAllBytes(path);
    }


    private List<MemberDto> initMembers() throws ParseException {
        final GeoJSONWriter geoJSONWriter = new GeoJSONWriter();
        final WKTReader reader = new WKTReader();
        List<MemberDto> members = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                Geometry geometry = reader.read("POINT(%d %d)".formatted(i, j));
                members.add(new MemberDto("id-%d".formatted(i * 6 + j), geoJSONWriter.write(geometry), COLLECTION, timestamp, IS_VERSION_OF, Map.of()));
            }
        }
        return members;
    }

    private String transformToJson(String id, org.wololo.geojson.Geometry geometry, LocalDateTime timestamp) {
        return "{\"memberId\": \"%s\", \"geojsonGeometry\": %s, \"timestamp\":\"%s\"}".formatted(id, geometry, timestamp);
    }

}