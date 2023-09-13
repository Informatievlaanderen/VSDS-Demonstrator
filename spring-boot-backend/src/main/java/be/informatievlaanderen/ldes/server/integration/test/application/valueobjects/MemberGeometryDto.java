package be.informatievlaanderen.ldes.server.integration.test.application.valueobjects;

import org.wololo.geojson.Geometry;

import java.time.LocalDateTime;

public class MemberGeometryDto {
    private final String memberId;
    private final Geometry geojsonGeometry;
    private final LocalDateTime timestamp;

    public MemberGeometryDto(String memberId, Geometry geojsonGeometry, LocalDateTime timestamp) {
        this.memberId = memberId;
        this.geojsonGeometry = geojsonGeometry;
        this.timestamp = timestamp;
    }

    public String getMemberId() {
        return memberId;
    }

    public Geometry getGeojsonGeometry() {
        return geojsonGeometry;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
