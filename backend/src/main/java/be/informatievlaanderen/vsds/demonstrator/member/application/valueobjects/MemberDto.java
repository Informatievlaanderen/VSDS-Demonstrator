package be.informatievlaanderen.vsds.demonstrator.member.application.valueobjects;

import org.wololo.geojson.Geometry;

import java.time.LocalDateTime;
import java.util.Map;

public class MemberDto {
    private final String memberId;
    private final Geometry geojsonGeometry;
    private final String collection;
    private final LocalDateTime timestamp;
    private final String isVersionOf;
    private final Map<String, String> properties;

    public MemberDto(String memberId, Geometry geojsonGeometry, String collection, LocalDateTime timestamp, String isVersionOf, Map<String, String> properties) {
        this.memberId = memberId;
        this.geojsonGeometry = geojsonGeometry;
        this.collection = collection;
        this.timestamp = timestamp;
        this.isVersionOf = isVersionOf;
        this.properties = properties;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getCollection() {
        return collection;
    }

    public Geometry getGeojsonGeometry() {
        return geojsonGeometry;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getIsVersionOf() {
        return isVersionOf;
    }

    public Map<String, String> getProperties() {
        return properties;
    }
}
