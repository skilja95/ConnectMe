package com.example.skilja.connectme.model;


public class Message {

    private String emailUser;
    private String id_group;
    private String text;
    private long date;
    private boolean isDeleted;

    public Message(String emailUser, String id_group, String text, long date) {
        this.emailUser = emailUser;
        this.id_group = id_group;
        this.text = text;
        this.date = date;
        this.isDeleted = false;
    }

    public Message() {

    }

    public String getEmailUser() {
        return emailUser;
    }

    public void setEmailUser(String emailUser) {
        this.emailUser = emailUser;
    }

    public String getId_group() {
        return id_group;
    }

    public void setId_group(String id_group) {
        this.id_group = id_group;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
