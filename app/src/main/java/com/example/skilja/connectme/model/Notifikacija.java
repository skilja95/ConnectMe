package com.example.skilja.connectme.model;

public class Notifikacija {

    private String from;
    private String to;
    private String group;
    private String message;

    public Notifikacija(){

    }

    public Notifikacija(String from, String to, String group, String message) {
        this.from = from;
        this.to = to;
        this.group = group;
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }



}
