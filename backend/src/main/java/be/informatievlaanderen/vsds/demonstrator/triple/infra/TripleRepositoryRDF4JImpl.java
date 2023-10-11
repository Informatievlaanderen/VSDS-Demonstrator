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
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;

@org.springframework.stereotype.Repository
public class TripleRepositoryRDF4JImpl implements TripleRepository {
    private final GraphDBConfig graphDBConfig;
    private final RepositoryManager repositoryManager;
    private final Repository repository;

    public TripleRepositoryRDF4JImpl(GraphDBConfig graphDBConfig) {
        this.graphDBConfig = graphDBConfig;
        repositoryManager = new RemoteRepositoryManager(graphDBConfig.getUrl().substring(0, graphDBConfig.getUrl().length() - 13));
        repository = repositoryManager.getRepository(graphDBConfig.getRepositoryId());
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

            return new MemberDescription(id, model);
        } catch (Exception e) {
            throw new TripleFetchFailedException(id, e);
        }
    }
}
