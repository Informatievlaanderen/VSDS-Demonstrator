package be.informatievlaanderen.vsds.demonstrator.member.application.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Configuration
@ConfigurationProperties(prefix = "ldes")
public class StreamsConfig {
    private Map<String, EventStreamConfig> streams;

    public Map<String, EventStreamConfig> getStreams() {
        return streams;
    }

    public Optional<EventStreamConfig> getStream(String collection) {
        return Optional.ofNullable(streams.get(collection));
    }

    public List<String> getMemberTypes() {
        return streams.values().stream().map(EventStreamConfig::getMemberType).toList();
    }

    public void setStreams(Map<String, EventStreamConfig> streams) {
        this.streams = streams;
    }


}
