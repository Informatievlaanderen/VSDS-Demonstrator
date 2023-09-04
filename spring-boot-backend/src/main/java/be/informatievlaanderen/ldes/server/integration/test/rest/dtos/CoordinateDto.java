package be.informatievlaanderen.ldes.server.integration.test.rest.dtos;

public class CoordinateDto {
    private double lat;
    private double lng;

    public CoordinateDto() {
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
}
