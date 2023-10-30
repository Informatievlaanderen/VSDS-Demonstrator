package be.informatievlaanderen.vsds.demonstrator.member.presentation.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CoordinateDto {
    @JsonProperty(value = "lat")
    private double latitude;
    @JsonProperty(value = "lng")
    private double longitude;

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
