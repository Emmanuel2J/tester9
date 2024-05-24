package com.example.tester;

public class MainModel_service {
    private String name;
    private String position;
    private String email;
    private String image;

    private String username;
    public MainModel_service() {
        // Default constructor required for Firebase
    }

    public MainModel_service(String name, String position, String email, String image, String username) {
        this.name = name;
        this.position = position;
        this.email = email;
        this.image = image;
        this.username = username;
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
}
