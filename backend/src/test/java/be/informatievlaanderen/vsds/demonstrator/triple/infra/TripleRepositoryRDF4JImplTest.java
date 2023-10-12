package be.informatievlaanderen.vsds.demonstrator.triple.infra;

import be.informatievlaanderen.vsds.demonstrator.triple.domain.entities.MemberDescription;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.config.RepositoryConfig;
import org.eclipse.rdf4j.repository.manager.LocalRepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import org.eclipse.rdf4j.repository.sail.config.SailRepositoryConfig;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.sail.memory.config.MemoryStoreConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class TripleRepositoryRDF4JImplTest {

    private static final String MEMBER_ID = "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/#ID";
    private static final GraphDBConfig graphDbConfig = new GraphDBConfig();
    private static final String LOCAL_SERVER_URL = "http://localhost:8080/rdf4j-server";
    private static final String LOCAL_REPOSITORY_ID = "test";
    private TripleRepositoryRDF4JImpl repo;
    private static RepositoryManager manager;
    private static RepositoryConnection connection;

    @TempDir
    File dataDir;

    @BeforeEach
    public void setUp() {
        manager = new LocalRepositoryManager(dataDir);
        manager.init();
        manager.addRepositoryConfig(
                new RepositoryConfig(LOCAL_REPOSITORY_ID, new SailRepositoryConfig(new MemoryStoreConfig(true))));
        connection = manager.getRepository(LOCAL_REPOSITORY_ID).getConnection();

        graphDbConfig.setRepositoryId(LOCAL_REPOSITORY_ID);
        graphDbConfig.setUrl(LOCAL_SERVER_URL + "/repositories/");

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

        MemberDescription memberDescription = repo.getById(MEMBER_ID);

        assertEquals(MEMBER_ID, memberDescription.getMemberId());
        assertFalse(memberDescription.getModel().isEmpty());
    }

    @Test
    void when_MemberNotPresent_then_EmptyModelIsExpected() {
        MemberDescription memberDescription = repo.getById(MEMBER_ID);

        assertEquals(MEMBER_ID, memberDescription.getMemberId());
        assertTrue(memberDescription.getModel().isEmpty());
    }

    void populateRepository() throws IOException {
        var model = Rio.parse(readDataFromFile(), "", RDFFormat.NQUADS);
        connection.begin();
        connection.add(model);
        connection.commit();
        connection.close();
    }

    private InputStream readDataFromFile() throws IOException {
        Path path = ResourceUtils.getFile("classpath:members/mobility-hindrance.nq").toPath();
        return Files.newInputStream(path);
    }

}