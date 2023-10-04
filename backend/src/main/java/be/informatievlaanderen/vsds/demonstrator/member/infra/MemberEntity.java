package be.informatievlaanderen.vsds.demonstrator.member.infra;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.locationtech.jts.geom.Geometry;

import java.time.LocalDateTime;

@Entity(name = "member_entity")
public class MemberEntity {
    @Id
    private String memberId;
    private String collection;
    private Geometry geometry;
    private LocalDateTime timestamp;

    public MemberEntity() {
    }

    public MemberEntity(String memberId, String collection, Geometry geometry, LocalDateTime timestamp) {
        this.memberId = memberId;
        this.collection = collection;
        this.geometry = geometry;
        this.timestamp = timestamp;
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
}
