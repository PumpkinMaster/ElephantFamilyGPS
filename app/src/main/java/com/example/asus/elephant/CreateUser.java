package com.example.asus.elephant;

public class CreateUser {
    public double x, y;
    public String email, password, name, code, isSharing;
    public String userId;   // User ID according to Firebase.

    public CreateUser() {
        // default constructor.
    }

    // The Constructor!
    public CreateUser(String name, String email, String password, String code, String isSharing, double x, double y, String userId) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.code = code;
        this.isSharing = isSharing;
        this.x = x;
        this.y = y;
        this.userId = userId;
    }


}
