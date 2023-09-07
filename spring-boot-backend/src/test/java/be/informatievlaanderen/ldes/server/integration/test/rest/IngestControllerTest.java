package be.informatievlaanderen.ldes.server.integration.test.rest;

import be.informatievlaanderen.ldes.server.integration.test.domain.membergeometry.entities.MemberGeometry;
import be.informatievlaanderen.ldes.server.integration.test.domain.membergeometry.services.MemberGeometryService;
import be.informatievlaanderen.ldes.server.integration.test.rest.converters.MemberConverter;
import be.informatievlaanderen.ldes.server.integration.test.rest.dtos.MemberDTO;
import com.apicatalog.jsonld.http.media.MediaType;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.ResourceUtils;
import org.wololo.geojson.Geometry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = {IngestController.class, MemberConverter.class, MemberConverter.class})
class IngestControllerTest {

    @MockBean
    private MemberGeometryService service;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void when_MemberIsPosted_then_IngestMemberInService() throws Exception {
        Model model = RDFParser.source("members/mobility-hindrance.nq").lang(Lang.NQUADS).toModel();
        MemberDTO memberDTO = new MemberDTO(model);
        MemberGeometry memberGeometry = memberDTO.getMemberGeometry();

        mockMvc.perform(post("/members")
                        .content(readDataFromFile("members/mobility-hindrance.nq"))
                        .contentType(Lang.NQUADS.getHeaderString()))
                .andExpect(status().isOk());

        verify(service).ingestMemberGeometry(argThat(result -> result.getGeometry().equals(memberGeometry.getGeometry())));
    }

    private byte[] readDataFromFile(String filename) throws IOException {
        Path path = ResourceUtils.getFile("classpath:" + filename).toPath();
        return Files.readAllBytes(path);
    }
}