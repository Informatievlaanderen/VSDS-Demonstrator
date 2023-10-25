package be.informatievlaanderen.vsds.demonstrator.triple.application.services;

import be.informatievlaanderen.vsds.demonstrator.triple.domain.valueobjects.Node;
import be.informatievlaanderen.vsds.demonstrator.triple.domain.valueobjects.Triple;
import be.informatievlaanderen.vsds.demonstrator.triple.domain.repositories.TripleRepository;
import be.informatievlaanderen.vsds.demonstrator.triple.infra.exceptions.TripleFetchFailedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ResourceUtils;
import wiremock.com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TripleServiceImplTest {
    private static final String FILE_NAME = "members/mobility-hindrance.nq";
    private static final int NUMBER_OF_STATEMENTS = 36;
    private static final int NUMBER_OF_STATEMENTS_WITH_ID_AS_SUBJECT = 15;
    private static final String MEMBER_ID = "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/#ID";
    private static final String COLLECTION = "collection";
    @InjectMocks
    private TripleServiceImpl service;
    @Mock
    private TripleRepository repository;

    @Test
    void when_MemberIsPresent_then_RetrieveTriples() throws IOException {
        when(repository.getById(MEMBER_ID, COLLECTION)).thenReturn(readTriplesFromFile());

        var fetchedTriples = service.getTriplesById(MEMBER_ID, COLLECTION);

        assertThat(fetchedTriples)
                .hasSize(NUMBER_OF_STATEMENTS)
                .filteredOn(triple -> triple.getSubject().getValue().equals(MEMBER_ID))
                .hasSize(NUMBER_OF_STATEMENTS_WITH_ID_AS_SUBJECT);
        verify(repository).getById(MEMBER_ID, COLLECTION);
    }

    @Test
    void when_MemberHasEmptyModel_then_RetrieveEmptyList() {
        when(repository.getById(MEMBER_ID, COLLECTION)).thenReturn(List.of());

        var fetchedTriples = service.getTriplesById(MEMBER_ID, COLLECTION);

        assertThat(fetchedTriples).isEmpty();
        verify(repository).getById(MEMBER_ID, COLLECTION);
    }

    @Test
    void when_MemberCannotBeFetched_then_RetrieveEmptyList() {
        when(repository.getById(MEMBER_ID, COLLECTION)).thenThrow(new TripleFetchFailedException(MEMBER_ID, new RuntimeException()));

        assertThatThrownBy(() -> service.getTriplesById(MEMBER_ID, COLLECTION))
                .isInstanceOf(TripleFetchFailedException.class)
                .hasMessage("Something went wrong while trying to fetch the triples with id %s".formatted(MEMBER_ID));

        verify(repository).getById(MEMBER_ID, COLLECTION);
    }

    private List<Triple> readTriplesFromFile() throws IOException {
        File file = ResourceUtils.getFile("classpath:" + FILE_NAME);
        Function<String, String> transformer = str -> str.length() > 1 ? str.substring(1, str.length() - 1) : str;
        return Files.readLines(file, Charset.defaultCharset()).stream()
                .map(line -> line.split(" "))
                .map(strings -> Arrays.stream(strings).map(transformer).map(Node::new).toList())
                .map(nodes -> new Triple(nodes.get(0), nodes.get(1), nodes.get(2)))
                .toList();
    }
}