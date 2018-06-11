package com.example.skilja.connectme.model;

import java.util.ArrayList;

public class UserToken {

    private String user_id;
    private String email;
    private String first_name;
    private String last_name;
    private String imageURL;
    private String token;

    private ArrayList<Group> groups;

    public UserToken(String user_id, String email, String first_name, String last_name, String imageURL, ArrayList<Group> groups, String token) {
        this.user_id = user_id;
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
        this.imageURL = imageURL;
        this.groups = groups;
        this.token = token;
    }

    public UserToken(String user_id, String email, String first_name, String last_name, String imageURL, String token) {
        this.user_id = user_id;
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
        this.imageURL = imageURL;
        this.token = token;
    }

    public UserToken() {

    }

    public String getToken() {
        return token;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<Group> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<Group> groups) {
        this.groups = groups;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }


    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return first_name;
    }


}
