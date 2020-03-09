package com.pronuxtech.slynk;

public class LiveSearchModelClass {
    private String id, lower_name, username, profile_img;

    public LiveSearchModelClass() {

    }

    public LiveSearchModelClass(String id, String lower_name, String username, String profile_img) {
        this.id = id;
        this.lower_name = lower_name;
        this.username = username;
        this.profile_img = profile_img;
    }
//    GETTERS

    public String getId() {
        return id;
    }

    public String getLower_name() {
        return lower_name;
    }

    public String getUsername() {
        return username;
    }

    public String getProfile_img() {
        return profile_img;
    }


//    SETTERS


    public void setId(String id) {
        this.id = id;
    }

    public void setLower_name(String lower_name) {
        this.lower_name = lower_name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setProfile_img(String profile_img) {
        this.profile_img = profile_img;
    }
}
