package be.informatievlaanderen.vsds.demonstrator.member.infra;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.locationtech.jts.geom.Geometry;

import java.time.LocalDateTime;

@Entity
public class MemberEntity {
    @Id
    private String memberId;
    private Geometry geometry;
    private LocalDateTime timestamp;

    public MemberEntity() {
    }

    public MemberEntity(String memberId, Geometry geometry, LocalDateTime timestamp) {
        this.memberId = memberId;
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
}
