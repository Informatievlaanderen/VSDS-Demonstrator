package be.informatievlaanderen.vsds.demonstrator.triple.infra;

import be.informatievlaanderen.vsds.demonstrator.triple.domain.entities.MemberDescription;
import be.informatievlaanderen.vsds.demonstrator.triple.domain.repositories.TripleRepository;
import be.informatievlaanderen.vsds.demonstrator.triple.infra.exceptions.TripleFetchFailedException;
import org.eclipse.rdf4j.common.transaction.IsolationLevels;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.DynamicModel;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@org.springframework.stereotype.Repository
public class TripleRepositoryRDF4JImpl implements TripleRepository {
    private final GraphDBConfig graphDBConfig;
    private RepositoryManager repositoryManager;
    private Repository repository;
    private static final Logger log = LoggerFactory.getLogger(TripleRepositoryRDF4JImpl.class);

    public TripleRepositoryRDF4JImpl(GraphDBConfig graphDBConfig) {
        this.graphDBConfig = graphDBConfig;
        String url = graphDBConfig.getUrl().substring(0, graphDBConfig.getUrl().length() - 13);
        initRepo(new RemoteRepositoryManager(url));
    }

    protected void initRepo(RepositoryManager manager) {
        repositoryManager = manager;
        repositoryManager.init();
        try {
            if(!repositoryManager.hasRepositoryConfig(graphDBConfig.getRepositoryId())) {
                MemoryStoreConfig storeConfig = new MemoryStoreConfig(true);
                RepositoryImplConfig repositoryImplConfig = new SailRepositoryConfig(storeConfig);
                RepositoryConfig config = new RepositoryConfig(graphDBConfig.getRepositoryId(), repositoryImplConfig);
                repositoryManager.addRepositoryConfig(config);
                log.info("Created repository with id: {}", graphDBConfig.getRepositoryId());
            }
            repository = repositoryManager.getRepository(graphDBConfig.getRepositoryId());

        } catch (Exception e) {
            log.error("Could not create repository. Reason: {}", e.getMessage());
        }
    }

    @Override
    public MemberDescription getById(String id) {
        try (RepositoryConnection dbConnection = repository.getConnection()) {
            dbConnection.setIsolationLevel(IsolationLevels.NONE);
            dbConnection.begin();

            String queryString = "Describe<" + id + ">";
            GraphQueryResult result = dbConnection.prepareGraphQuery(queryString).evaluate();
            Model model = new DynamicModel(new DynamicModelFactory());
            result.forEach(model::add);
            dbConnection.commit();

            return new MemberDescription(id, model);
        } catch (Exception e) {
            throw new TripleFetchFailedException(id, e);
        }
    }
}
