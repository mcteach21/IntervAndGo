package mc.apps.demo0.model;

public class GpsPosition {
    private int id;
    private double latitude;
    private double longitude;
    private String technicien_id;

    public GpsPosition(int id, double latitude, double longitude, String technicien_id) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.technicien_id = technicien_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTechnicien_id() {
        return technicien_id;
    }

    public void setTechnicien_id(String technicien_id) {
        this.technicien_id = technicien_id;
    }

    @Override
    public String toString() {
        return technicien_id + " : " + latitude + "x" + longitude ;
    }
}
