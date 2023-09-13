package be.informatievlaanderen.vsds.demonstrator.rest;

import be.informatievlaanderen.vsds.demonstrator.member.domain.member.entities.Member;
import be.informatievlaanderen.vsds.demonstrator.member.application.services.MemberGeometryService;
import be.informatievlaanderen.vsds.demonstrator.member.application.config.StreamsConfig;
import be.informatievlaanderen.vsds.demonstrator.member.rest.converters.ModelHttpConverter;
import be.informatievlaanderen.vsds.demonstrator.member.application.valueobjects.IngestedMemberDto;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles({"test"})
@WebMvcTest
@ContextConfiguration(classes = {IngestController.class, ModelHttpConverter.class, ModelHttpConverter.class, StreamsConfig.class})
class IngestControllerTest {

    @MockBean
    private MemberGeometryService service;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private StreamsConfig streamsConfig;

    @Test
    void when_MemberIsPosted_then_IngestMemberInService() throws Exception {
        Model model = RDFParser.source("members/mobility-hindrance.nq").lang(Lang.NQUADS).toModel();
        IngestedMemberDto dto = new IngestedMemberDto(model);
        Member member = dto.getMemberGeometry(streamsConfig.getStreams());

        mockMvc.perform(post("/members")
                        .content(readDataFromFile("members/mobility-hindrance.nq"))
                        .contentType(Lang.NQUADS.getHeaderString()))
                .andExpect(status().isOk());

//        verify(service).ingestMemberGeometry(argThat(result -> result.getGeometry().equals(memberGeometry.getGeometry()) && result.getTimestamp().isEqual(memberGeometry.getTimestamp())));
    }

    private byte[] readDataFromFile(String filename) throws IOException {
        Path path = ResourceUtils.getFile("classpath:" + filename).toPath();
        return Files.readAllBytes(path);
    }
}