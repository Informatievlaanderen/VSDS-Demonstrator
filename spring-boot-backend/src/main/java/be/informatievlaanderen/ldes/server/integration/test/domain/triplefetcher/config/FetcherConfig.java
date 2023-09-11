package be.informatievlaanderen.ldes.server.integration.test.domain.triplefetcher.config;

import be.informatievlaanderen.ldes.server.integration.test.domain.triplefetcher.RepositoryTripleFetcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FetcherConfig {

    @Bean
    public RepositoryTripleFetcher tripleFetcher() {
        return new RepositoryTripleFetcher("test", "http://localhost:8080/rdf4j-server");
    }

}
