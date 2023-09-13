package be.informatievlaanderen.ldes.server.integration.test.rest.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class EventStreamConfig {
    private String memberType;
    private String timestampPath;

    public String getMemberType() {
        return memberType;
    }

    public void setMemberType(String collection) {
        this.memberType = collection;
    }

    public String getTimestampPath() {
        return timestampPath;
    }

    public void setTimestampPath(String timestampPath) {
        this.timestampPath = timestampPath;
    }
}