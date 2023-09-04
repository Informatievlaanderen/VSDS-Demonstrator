package be.informatievlaanderen.ldes.server.integration.test.domain.membergeometry.entities;


import org.locationtech.jts.geom.Geometry;

public class MemberGeometry {
    private final String memberId;
    private final Geometry geometry;

    public MemberGeometry(String memberId, Geometry geometry) {
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
