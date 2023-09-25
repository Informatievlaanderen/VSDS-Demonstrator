package be.informatievlaanderen.vsds.demonstrator.triple.infra;

import be.informatievlaanderen.vsds.demonstrator.triple.domain.entities.MemberDescription;
import be.informatievlaanderen.vsds.demonstrator.triple.domain.repositories.TripleRepository;
import org.eclipse.rdf4j.common.transaction.IsolationLevels;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ModelFactory;
import org.eclipse.rdf4j.model.impl.DynamicModelFactory;
import org.eclipse.rdf4j.query.GraphQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.config.RepositoryConfig;
import org.eclipse.rdf4j.repository.config.RepositoryImplConfig;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import org.eclipse.rdf4j.repository.sail.config.SailRepositoryConfig;
import org.eclipse.rdf4j.sail.memory.config.MemoryStoreConfig;

import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Repository
public class TripleRepositoryImplRepoManager implements TripleRepository {
    private final String repositoryId;
    private final RepositoryManager repositoryManager;
    private Repository repository;

    public TripleRepositoryImplRepoManager(GraphDBConfig config) {
        this.repositoryId = config.getRepositoryId();
        this.repositoryManager = new RemoteRepositoryManager(config.getUrl().replace("repositories/", ""));
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

    public MemberDescription getById(String id) {
        try (RepositoryConnection dbConnection = repository.getConnection()) {
            dbConnection.setIsolationLevel(IsolationLevels.NONE);
            dbConnection.begin();

            String queryString = "Describe " + id;


            try (GraphQueryResult queryResult = dbConnection.prepareGraphQuery(queryString).evaluate()) {

                Model model = new DynamicModelFactory().createEmptyModel();
                model.addAll(queryResult.stream().toList());

                return new MemberDescription(id, model);}
        }
    }
}
