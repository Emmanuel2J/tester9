package com.example.tester;

public class Banner {
    private String image_url; // Use "image_url" to match the key in Firebase

    public Banner() {
        // Default constructor required for calls to DataSnapshot.getValue(Banner.class)
    }

    public Banner(String image_url) {
        this.image_url = image_url;
    }

    public String getImageUrl() {
        return image_url;
    }

    public void setImageUrl(String image_url) {
        this.image_url = image_url;
    }
}
