package be.informatievlaanderen.vsds.demonstrator.triple.infra;

import be.informatievlaanderen.vsds.demonstrator.triple.domain.valueobjects.Triple;
import be.informatievlaanderen.vsds.demonstrator.triple.infra.exceptions.TripleFetchFailedException;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.config.RepositoryConfig;
import org.eclipse.rdf4j.repository.manager.LocalRepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import org.eclipse.rdf4j.repository.sail.config.SailRepositoryConfig;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.sail.memory.config.MemoryStoreConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

class TripleRepositoryRDF4JImplTest {

    private static final String MEMBER_ID = "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/#ID";
    private static final String COLLECTION = "collection";
    private static final GraphDBConfig graphDbConfig = new GraphDBConfig();
    private static final String LOCAL_SERVER_URL = "http://localhost:8080/rdf4j-server";
    private static final String LOCAL_REPOSITORY_ID = "test";
    private TripleRepositoryRDF4JImpl repo;
    private RepositoryManager manager;

    @BeforeEach
    void setUp() {
        graphDbConfig.setRepositoryId(LOCAL_REPOSITORY_ID);
        graphDbConfig.setUrl(LOCAL_SERVER_URL + "/repositories/");
    }

    @Nested
    class SuccessfulQuerying {
        @TempDir
        File dataDir;
        private RepositoryConnection connection;

        @BeforeEach
        public void setUp() {
            manager = new LocalRepositoryManager(dataDir);
            manager.init();
            manager.addRepositoryConfig(
                    new RepositoryConfig(LOCAL_REPOSITORY_ID, new SailRepositoryConfig(new MemoryStoreConfig(true))));
            connection = manager.getRepository(LOCAL_REPOSITORY_ID).getConnection();

            repo = new TripleRepositoryRDF4JImpl(graphDbConfig);
            repo.initRepo(manager);
        }

        @AfterEach
        public void tearDown() {
            connection.close();
            manager.shutDown();
        }

        @Test
        void when_ExistingTriplesAreRequested_then_MemberDescriptionIsExpected() throws IOException {
            populateRepository();

            List<Triple> triples = repo.getById(MEMBER_ID, COLLECTION);

            assertThat(triples).isNotEmpty();
        }

        @Test
        void when_MemberNotPresent_then_EmptyModelIsExpected() {
            List<Triple> triples = repo.getById(MEMBER_ID, COLLECTION);

            assertThat(triples).isEmpty();
        }

        void populateRepository() throws IOException {
            var model = Rio.parse(readDataFromFile(), "", RDFFormat.NQUADS);
            connection.begin();
            var namedGraphIRI = connection.getValueFactory().createIRI("http://" + COLLECTION);
            connection.add(model, namedGraphIRI);
            connection.commit();
            connection.close();
        }
    }

    @Nested
    class FailedQuerying {
        private Repository repository;

        @BeforeEach
        public void setUp() {
            manager = mock(RepositoryManager.class);
            repository = mock(Repository.class);

            when(manager.getRepository(LOCAL_REPOSITORY_ID)).thenReturn(repository);

            repo = new TripleRepositoryRDF4JImpl(graphDbConfig);
            repo.initRepo(manager);
        }

        @Test
        void when_somethingWentWrongWhileFetching_then_ThrowTripleFetchException() {
            when(repository.getConnection()).thenThrow(RepositoryException.class);

            assertThatThrownBy(() -> repo.getById(MEMBER_ID, COLLECTION))
                    .isInstanceOf(TripleFetchFailedException.class)
                    .hasMessage("Something went wrong while trying to fetch the triples with id %s".formatted(MEMBER_ID));
        }
    }

    private InputStream readDataFromFile() throws IOException {
        Path path = ResourceUtils.getFile("classpath:members/mobility-hindrance.nq").toPath();
        return Files.newInputStream(path);
    }

}