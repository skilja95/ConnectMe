package com.example.skilja.connectme.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Group {

    private String id_group;
    private String title;
    private HashMap<String, User> members;
    private List<Message> messages;
    private int color;

    public Group(String uid, String title, HashMap<String, User> members, int color) {
        this.id_group = uid;
        this.title = title;
        this.members = members;
        this.messages = new ArrayList<>();
        this.color = color;
    }

    public Group() {
        super();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public HashMap<String, User> getMembers() {
        return members;
    }

    public void setMembers(HashMap<String, User> members) {
        this.members = members;
    }

    public String getId_group() {
        return id_group;
    }

    public void setId_group(String id_group) {
        this.id_group = id_group;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
