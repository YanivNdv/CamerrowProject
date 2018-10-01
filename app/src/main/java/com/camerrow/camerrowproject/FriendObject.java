package com.camerrow.camerrowproject;

public class FriendObject {

    private String name;
    private String username;
    private String key;
    private String image;
    private double latitude;
    private double longitude;

    public FriendObject(String name, String username, String key, String image, double latitude, double longitude) {
        this.name = name;
        this.username = username;
        this.image = image;
        this.key = key;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public FriendObject() {
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
