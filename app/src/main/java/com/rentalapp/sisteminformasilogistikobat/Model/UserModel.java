package com.rentalapp.sisteminformasilogistikobat.Model;

public class UserModel {
    private String userId,username, password,  name, photo;
    private int level;

    public UserModel() {
    }

    public UserModel(String userId, String username, String password, String name, String photo, int level) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.name = name;
        this.photo = photo;
        this.level = level;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
