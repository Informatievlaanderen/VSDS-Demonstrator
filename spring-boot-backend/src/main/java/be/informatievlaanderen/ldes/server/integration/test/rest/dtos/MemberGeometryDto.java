package be.informatievlaanderen.ldes.server.integration.test.rest.dtos;

import org.wololo.geojson.Geometry;

public class MemberGeometryDto {
    private final String memberId;
    private final Geometry geojsonGeometry;

    public MemberGeometryDto(String memberId, Geometry geojsonGeometry) {
        this.memberId = memberId;
        this.geojsonGeometry = geojsonGeometry;
    }

    public String getMemberId() {
        return memberId;
    }

    public Geometry getGeojsonGeometry() {
        return geojsonGeometry;
    }
}
