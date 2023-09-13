package be.informatievlaanderen.ldes.server.integration.test.rest.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

public class MapBoundsDto {
    @JsonProperty(value = "_northEast")
    private CoordinateDto northEast;
    @JsonProperty(value = "_southWest")
    private CoordinateDto southWest;


    public void setNorthEast(CoordinateDto northEast) {
        this.northEast = northEast;
    }

    public void setSouthWest(CoordinateDto southWest) {
        this.southWest = southWest;
    }

    public Geometry getGeometry() {
        Coordinate[] coordinates = new Coordinate[]{
                new Coordinate(southWest.getLongitude(), southWest.getLatitude()),
                new Coordinate(southWest.getLongitude(), northEast.getLatitude()),
                new Coordinate(northEast.getLongitude(), northEast.getLatitude()),
                new Coordinate(northEast.getLongitude(), southWest.getLatitude()),
                new Coordinate(southWest.getLongitude(), southWest.getLatitude())
        };
        return new GeometryFactory().createPolygon(coordinates);
    }
}
