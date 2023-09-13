package be.informatievlaanderen.ldes.server.integration.test.rest.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "ldes")
public class StreamsConfig {
    private List<EventStreamConfig> streams;

    public List<EventStreamConfig> getStreams() {
        return streams;
    }

    public List<String> getCollectionNames() {
        return streams.stream().map(EventStreamConfig::getMemberType).toList();
    }

    public void setStreams(List<EventStreamConfig> streams) {
        this.streams = streams;
    }


}
