package com.ensias.ebarber.model;

public class MarkerData {
    Double latitude,longitude,distance;
    String title,gmail;


    public MarkerData(Double latitude, Double longitude,Double distance, String title,String gmail) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.gmail = gmail;
        this.distance = distance;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getGmail() {
        return gmail;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
