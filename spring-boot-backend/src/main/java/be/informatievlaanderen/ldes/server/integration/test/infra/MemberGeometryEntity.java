package be.informatievlaanderen.ldes.server.integration.test.infra;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.locationtech.jts.geom.Geometry;

import java.time.LocalDateTime;

@Entity
public class MemberGeometryEntity {
    @Id
    private String memberId;
    private Geometry geometry;
    private LocalDateTime timestamp;

    public MemberGeometryEntity() {
    }

    public MemberGeometryEntity(String memberId, Geometry geometry, LocalDateTime timestamp) {
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
