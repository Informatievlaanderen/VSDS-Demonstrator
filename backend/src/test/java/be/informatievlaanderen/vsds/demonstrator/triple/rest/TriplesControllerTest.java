package be.informatievlaanderen.vsds.demonstrator.triple.rest;

import be.informatievlaanderen.vsds.demonstrator.triple.application.services.TripleService;
import be.informatievlaanderen.vsds.demonstrator.triple.domain.valueobjects.Node;
import be.informatievlaanderen.vsds.demonstrator.triple.domain.valueobjects.Triple;
import be.informatievlaanderen.vsds.demonstrator.triple.infra.exceptions.TripleFetchFailedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = {TriplesController.class, TripleExceptionHandler.class})
class TriplesControllerTest {
    private static final String ID = "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464";

    @MockBean
    private TripleService tripleService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void when_triplesFetchingSucceeded_then_ReturnStatus200() throws Exception {
        List<Triple> triples = readTriplesFromFile();
        String json = new ObjectMapper().writeValueAsString(triples);
        when(tripleService.getTriplesById(ID)).thenReturn(triples);

        mockMvc.perform(get(URI.create("/api/triples/" + ID)))
                .andExpect(status().isOk())
                .andExpect(content().json(json));

        verify(tripleService).getTriplesById(ID);
    }

    @Test
    void when_triplesFetchingFails_then_ReturnStatus404() throws Exception {
        when(tripleService.getTriplesById(ID)).thenThrow(new TripleFetchFailedException(ID, new RuntimeException()));

        mockMvc.perform(get(URI.create("/api/triples/" + ID)))
                .andExpect(status().isNotFound());

        verify(tripleService).getTriplesById(ID);
    }

    private List<Triple> readTriplesFromFile() throws IOException {
        Path path = ResourceUtils.getFile("classpath:members/mobility-hindrance.nq").toPath();
        Function<String, String> transformer = str -> str.length() > 1 ? str.substring(1, str.length() - 1) : str;

        return Files.readAllLines(path).stream()
                .map(str -> str.split(" "))
                .map(strings -> Arrays.stream(strings).map(transformer).map(Node::new).toList())
                .map(nodes -> new Triple(nodes.get(0), nodes.get(1), nodes.get(2)))
                .toList();
    }
}