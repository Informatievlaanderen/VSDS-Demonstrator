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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MemberGeometry that = (MemberGeometry) o;

        return memberId.equals(that.memberId);
    }

    @Override
    public int hashCode() {
        return memberId.hashCode();
    }
}
