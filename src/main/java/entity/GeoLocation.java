package entity;

/**
 * Represents geographical coordinates (latitude and longitude).
 */
public class GeoLocation {
    private final double latitude;
    private final double longitude;

    public GeoLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}

