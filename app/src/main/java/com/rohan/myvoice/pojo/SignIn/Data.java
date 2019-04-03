package com.rohan.myvoice.pojo.SignIn;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rohan.myvoice.pojo.user_profile.Profile;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("profile")
    @Expose
    private Profile profile;
    @SerializedName("AskPermission")
    @Expose
    private String askPermission;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public String getAskPermission() {
        return askPermission;
    }

    public void setAskPermission(String askPermission) {
        this.askPermission = askPermission;
    }

}