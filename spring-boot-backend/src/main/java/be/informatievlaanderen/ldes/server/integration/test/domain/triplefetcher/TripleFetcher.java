package be.informatievlaanderen.ldes.server.integration.test.domain.triplefetcher;


import be.informatievlaanderen.ldes.server.integration.test.rest.dtos.TripleDTO;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class TripleFetcher {


    private final String repositoryId;
    private final String urlString; //"http://localhost:8080/rdf4j-server/repositories/"

    public TripleFetcher(String repositoryId, String hostUrl) {
        this.repositoryId = repositoryId;
        urlString = hostUrl;
    }

    public List<TripleDTO> fetchNodeHttp(String id) throws IOException {
        List<TripleDTO> triples = new ArrayList<>();
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(urlString + repositoryId);

// Request parameters and other properties.

        String queryString = "Describe<" + id + ">";
        List<NameValuePair> params = new ArrayList<NameValuePair>(1);
        params.add(new BasicNameValuePair("query", queryString));
        httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

//Execute and get the response.
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();

        if (entity != null) {
            try (InputStream instream = entity.getContent()) {
                Model model = Rio.parse(instream, RDFFormat.NTRIPLES);
                model.forEach(statement -> {
                    triples.add(new TripleDTO(statement.getSubject().stringValue(), statement.getPredicate().stringValue(), statement.getObject().stringValue()));
                });
            }
        }
        return triples;
    }
}
