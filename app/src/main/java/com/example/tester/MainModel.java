package com.example.tester;

public class MainModel {
    private String name;
    private String position;
    private String email;
    private String image;

    private String latitude;
    private String longitude;

    private String username;
    public MainModel() {
        // Default constructor required for Firebase
    }

    public MainModel(String name, String position, String email, String image, String username, String latitude, String longitude) {
        this.name = name;
        this.position = position;
        this.email = email;
        this.image = image;
        this.username = username;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public String getEmail() {
        return email;
    }

    public String getImage() {
        return image;
    }

    public String getUsername() {
        return username; // Getter method for username
    }
    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }
}
