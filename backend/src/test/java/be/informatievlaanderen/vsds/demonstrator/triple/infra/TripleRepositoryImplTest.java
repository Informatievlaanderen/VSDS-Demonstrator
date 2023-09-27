package be.informatievlaanderen.vsds.demonstrator.triple.infra;

import be.informatievlaanderen.vsds.demonstrator.triple.domain.entities.MemberDescription;
import be.informatievlaanderen.vsds.demonstrator.triple.domain.repositories.TripleRepository;
import be.informatievlaanderen.vsds.demonstrator.triple.infra.exceptions.TripleFetchFailedException;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;
import wiremock.com.fasterxml.jackson.databind.JsonNode;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@WireMockTest(httpPort = 8189)
class TripleRepositoryImplTest {
    private static final String MEMBER_ID = "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/#ID";
    private static final String ENDPOINT = "/rdf4j-server/repositories/test";
    private static final GraphDBConfig graphDbConfig = new GraphDBConfig();
    private TripleRepository repo;

    @BeforeAll
    static void beforeAll() {
        graphDbConfig.setUrl("http://localhost:8189/rdf4j-server/repositories/");
        graphDbConfig.setRepositoryId("test");
    }

    @BeforeEach
    void setUp() {
        repo = new TripleRepositoryImpl(graphDbConfig);
    }

    @Test
    void when_ExistingTriplesAreRequested_then_MemberDescriptionIsExpected() throws IOException {
        stubFor(post(ENDPOINT).willReturn(ok().withBody(readDataFromFile())));

        MemberDescription memberDescription = repo.getById(MEMBER_ID);

        assertEquals(MEMBER_ID, memberDescription.getMemberId());
        assertFalse(memberDescription.getModel().isEmpty());
        verify(postRequestedFor(urlEqualTo(ENDPOINT)));
    }

    @Test
    void when_BadRequestIsSent_then_EmptyModelIsExpected() {
        stubFor(post(ENDPOINT).willReturn(badRequest()));

        MemberDescription memberDescription = repo.getById(MEMBER_ID);

        assertEquals(MEMBER_ID, memberDescription.getMemberId());
        assertTrue(memberDescription.getModel().isEmpty());
        verify(postRequestedFor(urlEqualTo(ENDPOINT)));
    }

    @Test
    void when_RequestFails_then_ExceptionIsExpected() {
        stubFor(post(ENDPOINT).willReturn(badRequest().withFault(Fault.EMPTY_RESPONSE)));

        assertThrows(TripleFetchFailedException.class, () -> repo.getById(MEMBER_ID));
        verify(postRequestedFor(urlEqualTo(ENDPOINT)));
    }

    private byte[] readDataFromFile() throws IOException {
        Path path = ResourceUtils.getFile("classpath:members/mobility-hindrance.nq").toPath();
        return Files.readAllBytes(path);
    }
}