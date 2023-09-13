package be.informatievlaanderen.ldes.server.integration.test.domain.triplefetcher.services;

import be.informatievlaanderen.ldes.server.integration.test.rest.config.GraphDBConfig;
import be.informatievlaanderen.ldes.server.integration.test.rest.dtos.TripleDTO;
import org.eclipse.rdf4j.common.transaction.IsolationLevels;
import org.eclipse.rdf4j.query.GraphQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.config.RepositoryConfig;
import org.eclipse.rdf4j.repository.config.RepositoryImplConfig;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import org.eclipse.rdf4j.repository.sail.config.SailRepositoryConfig;
import org.eclipse.rdf4j.sail.memory.config.MemoryStoreConfig;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RepositoryTripleFetcher implements RdfFetcherService{
    private final String repositoryId;
    private final RepositoryManager repositoryManager;
    private Repository repository;

    public RepositoryTripleFetcher(GraphDBConfig config) {
        this.repositoryId = config.getRepositoryId();
        this.repositoryManager = new RemoteRepositoryManager(config.getUrl());
        init();
    }

    public void init() {
        repositoryManager.init();
        if(!repositoryManager.hasRepositoryConfig(repositoryId)) {
            MemoryStoreConfig storeConfig = new MemoryStoreConfig();
            RepositoryImplConfig repositoryImplConfig = new SailRepositoryConfig(storeConfig);
            RepositoryConfig config = new RepositoryConfig(repositoryId, repositoryImplConfig);
            repositoryManager.addRepositoryConfig(config);
        }
        repository = repositoryManager.getRepository(repositoryId);
    }

    public List<TripleDTO> fetchNode(String id) {
        List<TripleDTO> triples = new ArrayList<>();
        try (RepositoryConnection dbConnection = repository.getConnection()) {
            dbConnection.setIsolationLevel(IsolationLevels.NONE);
            dbConnection.begin();

            String queryString = "Describe " + id;


            try (GraphQueryResult queryResult = dbConnection.prepareGraphQuery(queryString).evaluate()) {
                queryResult.forEach(statement -> triples.add(new TripleDTO(statement.getSubject().stringValue(), statement.getPredicate().stringValue(), statement.getObject().stringValue())));
            }
        }

        return triples;
    }
}
