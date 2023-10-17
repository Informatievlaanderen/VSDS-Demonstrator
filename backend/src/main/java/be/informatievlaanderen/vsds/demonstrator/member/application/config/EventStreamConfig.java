package be.informatievlaanderen.vsds.demonstrator.member.application.config;

import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class EventStreamConfig {
    private String memberType;
    private String timestampPath;
    private Map<String, String> propertyPredicates;
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

    public Map<String, String> getPropertyPredicates() {
        if(propertyPredicates == null) {
            propertyPredicates = new HashMap<>();
        }
        return propertyPredicates;
    }

    public void setPropertyPredicates(Map<String, String> propertyPredicates) {
        this.propertyPredicates = propertyPredicates;
    }
}