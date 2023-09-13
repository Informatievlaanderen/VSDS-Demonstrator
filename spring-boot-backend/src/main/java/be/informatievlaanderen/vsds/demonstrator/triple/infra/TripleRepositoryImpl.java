package be.informatievlaanderen.vsds.demonstrator.triple.infra;

import be.informatievlaanderen.vsds.demonstrator.triple.domain.entities.MemberDescription;
import be.informatievlaanderen.vsds.demonstrator.triple.domain.repositories.TripleRepository;
import be.informatievlaanderen.vsds.demonstrator.triple.infra.exceptions.TripleFetchFailedException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.TreeModelFactory;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class TripleRepositoryImpl implements TripleRepository {
    private final GraphDBConfig graphDBConfig;

    public TripleRepositoryImpl(GraphDBConfig graphDBConfig) {
        this.graphDBConfig = graphDBConfig;
    }

    @Override
    public MemberDescription getById(String id) {
        HttpClient httpclient = HttpClients.createDefault();

        try {
            HttpPost httppost = new HttpPost(graphDBConfig.getUrl() + graphDBConfig.getRepositoryId());

// Request parameters and other properties.

            String queryString = "Describe<" + id + ">";
            List<NameValuePair> params = new ArrayList<>(1);
            params.add(new BasicNameValuePair("query", queryString));
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

//Execute and get the response.
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                Model model = Rio.parse(entity.getContent(), RDFFormat.NTRIPLES);
                return new MemberDescription(id, model);
            }
            return new MemberDescription(id, new TreeModelFactory().createEmptyModel());
        } catch (Exception e) {
            throw new TripleFetchFailedException(id, e);
        }
    }
}
