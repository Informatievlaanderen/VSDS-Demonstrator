package be.informatievlaanderen.vsds.demonstrator.member.domain.member.entities;


import org.locationtech.jts.geom.Geometry;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Member {
    private final String memberId;
    private final String collection;
    private final Geometry geometry;
    private final LocalDateTime timestamp;
    private final Map<String, String> properties;

    public Member(String memberId, String collection, Geometry geometry, LocalDateTime timestamp) {
        this.memberId = memberId;
        this.collection = collection;
        this.geometry = geometry;
        this.timestamp = timestamp;
        properties = new HashMap<>();
    }

    public Member(String memberId, String collection, Geometry geometry, LocalDateTime timestamp, Map<String, String> properties) {
        this.memberId = memberId;
        this.collection = collection;
        this.geometry = geometry;
        this.timestamp = timestamp;
        this.properties = properties;
    }

    public String getMemberId() {
        return memberId;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getCollection() {
        return collection;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Member that = (Member) o;

        return memberId.equals(that.memberId);
    }

    @Override
    public int hashCode() {
        return memberId.hashCode();
    }

}
