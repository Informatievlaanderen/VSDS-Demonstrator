package be.informatievlaanderen.ldes.server.integration.test.rest;

import be.informatievlaanderen.ldes.server.integration.test.domain.exceptions.ResourceNotFoundException;
import be.informatievlaanderen.ldes.server.integration.test.domain.membergeometry.services.MemberGeometryService;
import be.informatievlaanderen.ldes.server.integration.test.rest.config.GraphDBConfig;
import be.informatievlaanderen.ldes.server.integration.test.rest.config.MemberGeometryExceptionHandler;
import be.informatievlaanderen.ldes.server.integration.test.rest.dtos.MemberGeometryDto;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.ResourceUtils;
import org.wololo.jts2geojson.GeoJSONWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = {MemberRDFController.class, MemberGeometryExceptionHandler.class, GraphDBConfig.class})
class MemberRDFControllerTest {
    private static final String ID = "member-id";
    private static org.wololo.geojson.Geometry geoJSON;

    @MockBean
    private MemberGeometryService service;

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    static void beforeAll() throws ParseException {
        Geometry geometry = new WKTReader().read("POINT(5 5)");
        geoJSON = new GeoJSONWriter().write(geometry);
    }

    @Nested
    class GetSingleMemberGeometry {
        @Test
        void when_MemberGeometryPresent_then_MemberGeomtryIsReturned_and_StatusIs200() throws Exception {
            final String json = "{\"memberId\": \"%s\", \"geojsonGeometry\": %s}".formatted(ID, geoJSON.toString());
            MemberGeometryDto dto = new MemberGeometryDto(ID, geoJSON);

            when(service.getMemberById(ID)).thenReturn(dto);

            mockMvc.perform(get("/geometry/" + ID).accept(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(json))
                    .andExpect(status().isOk());

            verify(service).getMemberById(ID);
        }

        @Test
        void when_MemberGeometryIsNotPresent_then_StatusIs404() throws Exception {
            when(service.getMemberById(ID)).thenThrow(ResourceNotFoundException.class);

            mockMvc.perform(get("/geometry/" + ID).accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(service).getMemberById(ID);
        }
    }

    @Test
    void when_MapBoundariesArePosted_then_ReturnMemberGeometries() throws Exception {
        Geometry rectangle = new WKTReader().read("POLYGON((0 0, 0 5, 5 5, 5 0, 0 0))");
        List<MemberGeometryDto> members = initMembers();
        String json = "[" + members.stream()
                .map(dto -> "{'memberId': %s, 'geojsonGeometry': %s}".formatted(dto.getMemberId(), dto.getGeojsonGeometry().toString()))
                .collect(Collectors.joining(", ")) + "]";

        when(service.getMembersInRectangle(rectangle)).thenReturn(members);

        mockMvc.perform(post("/in-rectangle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(readMapBounds()))
                .andExpect(content().json(json))
                .andExpect(status().isOk());

        verify(service).getMembersInRectangle(rectangle);
    }

    private byte[] readMapBounds() throws IOException {
        Path path = ResourceUtils.getFile("classpath:mapbounds/rectangle.json").toPath();
        return Files.readAllBytes(path);
    }


    private List<MemberGeometryDto> initMembers() throws ParseException {
        final GeoJSONWriter geoJSONWriter = new GeoJSONWriter();
        final WKTReader reader = new WKTReader();
        List<MemberGeometryDto> members = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                Geometry geometry = reader.read("POINT(%d %d)".formatted(i, j));
                members.add(new MemberGeometryDto("id-%d".formatted(i * 6 + j), geoJSONWriter.write(geometry)));
            }
        }
        return members;
    }

}