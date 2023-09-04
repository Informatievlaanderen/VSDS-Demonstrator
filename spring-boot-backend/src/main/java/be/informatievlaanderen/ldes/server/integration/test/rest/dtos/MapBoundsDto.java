package be.informatievlaanderen.ldes.server.integration.test.rest.dtos;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

public class MapBoundsDto {
    private CoordinateDto _northEast;
    private CoordinateDto _southWest;

    public MapBoundsDto() {
    }

    public void set_northEast(CoordinateDto _northEast) {
        this._northEast = _northEast;
    }

    public void set_southWest(CoordinateDto _southWest) {
        this._southWest = _southWest;
    }

    public Geometry getGeometry() {
        Coordinate [] coordinates = new Coordinate[] {
                new Coordinate(_southWest.getLng(), _southWest.getLat()),
                new Coordinate(_southWest.getLng(), _northEast.getLat()),
                new Coordinate(_northEast.getLng(), _northEast.getLat()),
                new Coordinate(_northEast.getLng(), _southWest.getLat()),
                new Coordinate(_southWest.getLng(), _southWest.getLat())
        };
        return new GeometryFactory().createPolygon(coordinates);
    }
}
