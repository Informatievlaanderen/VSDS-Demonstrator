package be.informatievlaanderen.ldes.server.integration.test.infra;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.locationtech.jts.geom.Geometry;

@Entity
public class MemberGeometryEntity {
    @Id
    private String memberId;
    private Geometry geometry;

    public MemberGeometryEntity() {
    }

    public MemberGeometryEntity(String memberId, Geometry geometry) {
        this.memberId = memberId;
        this.geometry = geometry;
    }

    public String getMemberId() {
        return memberId;
    }

    public Geometry getGeometry() {
        return geometry;
    }
}
