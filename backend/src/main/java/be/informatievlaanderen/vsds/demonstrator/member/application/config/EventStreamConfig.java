package be.informatievlaanderen.vsds.demonstrator.member.application.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class EventStreamConfig {
    private String name;
    private String memberType;
    private String timestampPath;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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