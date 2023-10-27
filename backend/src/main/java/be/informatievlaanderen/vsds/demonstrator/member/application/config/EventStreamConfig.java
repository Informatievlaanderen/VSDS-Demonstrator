package be.informatievlaanderen.vsds.demonstrator.member.application.config;

import org.springframework.context.annotation.Configuration;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class EventStreamConfig {
    private String memberType;
    private String versionOfPath;
    private String timestampPath;
    private String timezoneId;
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
        if (propertyPredicates == null) {
            propertyPredicates = new HashMap<>();
        }
        return propertyPredicates;
    }

    public void setPropertyPredicates(Map<String, String> propertyPredicates) {
        this.propertyPredicates = propertyPredicates;
    }

    public ZoneId getTimezoneId() {
        if (timezoneId == null) {
            return ZoneOffset.UTC;
        }
        return ZoneId.of(timezoneId);
    }

    public void setTimezoneId(String timezoneId) {
        this.timezoneId = timezoneId;
    }

    public String getVersionOfPath() {
        return versionOfPath;
    }

    public void setVersionOfPath(String versionOfPath) {
        this.versionOfPath = versionOfPath;
    }
}