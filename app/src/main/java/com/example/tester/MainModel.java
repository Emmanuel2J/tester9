package com.example.tester;

public class MainModel {
    private String name;
    private String position;
    private String email;
    private String image;

    public MainModel() {
        // Default constructor required for Firebase
    }

    public MainModel(String name, String position, String email, String image) {
        this.name = name;
        this.position = position;
        this.email = email;
        this.image = image;
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
}
