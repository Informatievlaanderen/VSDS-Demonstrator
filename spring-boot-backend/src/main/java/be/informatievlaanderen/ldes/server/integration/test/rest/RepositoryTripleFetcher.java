package be.informatievlaanderen.ldes.server.integration.test.rest;

import be.informatievlaanderen.ldes.server.integration.test.rest.dtos.TripleDTO;
import org.eclipse.rdf4j.common.transaction.IsolationLevels;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;

import java.util.ArrayList;
import java.util.List;

public class RepositoryTripleFetcher {
    private final String repositoryId;
    private final String urlString = "http://localhost:8080/rdf4j-server/repositories/";
    //    private final RepositoryManager repositoryManager;
    private Repository repository;

    public RepositoryTripleFetcher(String repositoryId, String hostUrl) {
        this.repositoryId = repositoryId;
//        this.repositoryManager = new RemoteRepositoryManager(hostUrl);
    }

    public void init() {

//        final Repository repository = repositoryManager.getRepository(repositoryId);
    }

    public List<TripleDTO> fetchNode(String id) {
        List<TripleDTO> triples = new ArrayList<>();
        try (RepositoryConnection dbConnection = repository.getConnection()) {
            dbConnection.setIsolationLevel(IsolationLevels.NONE);
            dbConnection.begin();

            String queryString = "Describe " + id;


            dbConnection.prepareTupleQuery(queryString).evaluate().forEach(res -> {
                res.size();
            });
        }

        return triples;
    }
}
