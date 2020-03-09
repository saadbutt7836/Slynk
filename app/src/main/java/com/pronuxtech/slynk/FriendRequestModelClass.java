package com.pronuxtech.slynk;

public class FriendRequestModelClass {
    private String notification, sent_by, sent_to;
    private Double timestamp;

    public FriendRequestModelClass() {
    }

    public FriendRequestModelClass(String notification, String sent_by, String sent_to, Double timestamp) {
        this.notification = notification;
        this.sent_by = sent_by;
        this.sent_to = sent_to;
        this.timestamp = timestamp;
    }

//    GETTERS


    public String getNotification() {
        return notification;
    }

    public String getSent_by() {
        return sent_by;
    }

    public String getSent_to() {
        return sent_to;
    }

    public Double getTimestamp() {
        return timestamp;
    }

//    SETTERS


    public void setNotification(String notification) {
        this.notification = notification;
    }

    public void setSent_by(String sent_by) {
        this.sent_by = sent_by;
    }

    public void setSent_to(String sent_to) {
        this.sent_to = sent_to;
    }

    public void setTimestamp(Double timestamp) {
        this.timestamp = timestamp;
    }
}
