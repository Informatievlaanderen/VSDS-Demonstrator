package be.informatievlaanderen.vsds.demonstrator.triple.application.services;

import be.informatievlaanderen.vsds.demonstrator.triple.application.valueobjects.Triple;
import be.informatievlaanderen.vsds.demonstrator.triple.domain.entities.MemberDescription;
import be.informatievlaanderen.vsds.demonstrator.triple.domain.repositories.TripleRepository;
import be.informatievlaanderen.vsds.demonstrator.triple.infra.exceptions.TripleFetchFailedException;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.TreeModelFactory;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ResourceUtils;
import wiremock.com.google.common.io.Files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TripleServiceImplTest {
    private static final String MEMBER_ID = "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/#ID";
    @Mock
    private TripleRepository repository;
    private TripleService service;

    @BeforeEach
    void setUp() {
        service = new TripleServiceImpl(repository);
    }

    @Test
    void when_MemberIsPresent_then_RetrieveTriples() throws IOException {
        when(repository.getById(MEMBER_ID)).thenReturn(new MemberDescription(MEMBER_ID, readModelFromFile()));

        var triples = readTriplesFromFile();
        var fetchedTriples = service.getTriplesById(MEMBER_ID);

        Predicate<Triple> subjectPredicate = triple -> triple.getSubject().equals(MEMBER_ID);
        assertEquals(triples.size(), fetchedTriples.size());
        assertEquals(triples.stream().filter(subjectPredicate).count(), fetchedTriples.stream().filter(subjectPredicate).count());
        verify(repository).getById(MEMBER_ID);
    }

    @Test
    void when_MemberHasEmptyModel_then_RetrieveEmptyList() {
        when(repository.getById(MEMBER_ID)).thenReturn(new MemberDescription(MEMBER_ID, new TreeModelFactory().createEmptyModel()));

        var fetchedTriples = service.getTriplesById(MEMBER_ID);

        assertEquals(0, fetchedTriples.size());
        verify(repository).getById(MEMBER_ID);
    }

    @Test
    void when_MemberCannotBeFetched_then_RetrieveEmptyList() {
        when(repository.getById(MEMBER_ID)).thenThrow(new TripleFetchFailedException(MEMBER_ID, new RuntimeException()));

        assertThrows(TripleFetchFailedException.class, () -> service.getTriplesById(MEMBER_ID));
        verify(repository).getById(MEMBER_ID);
    }


    private Model readModelFromFile() throws IOException {
        File file = ResourceUtils.getFile("classpath:members/mobility-hindrance.nq");
        return Rio.parse(new FileInputStream(file), RDFFormat.NQUADS);
    }

    private List<Triple> readTriplesFromFile() throws IOException {
        File file = ResourceUtils.getFile("classpath:members/mobility-hindrance.nq");
        Function<String, String> transformer = str -> str.length() > 1 ? str.substring(1, str.length() - 1) : str;
        return Files.readLines(file, Charset.defaultCharset()).stream()
                .map(line -> line.split(" "))
                .map(strings -> Arrays.stream(strings).map(transformer).toList())
                .map(strings -> new Triple(strings.get(0), strings.get(1), strings.get(2)))
                .toList();
    }
}