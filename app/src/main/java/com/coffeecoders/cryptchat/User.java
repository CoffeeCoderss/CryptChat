package com.coffeecoders.cryptchat;

public class User {
    private String uID, name, phoneNumber, profileImage;

    public User() {

    }

    public User(String uID, String name, String phoneNumber, String profileImage) {
        this.uID = uID;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.profileImage = profileImage;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
