package com.pronuxtech.slynk;

public class NotificationsModelClass {
    private int seen, type;
    private String post_id, sent_by, sent_to;
    private Double timestamp;

    public NotificationsModelClass() {

    }

    public NotificationsModelClass(int seen, int type, String post_id, String sent_by, String sent_to, Double timestamp) {
        this.seen = seen;
        this.type = type;
        this.post_id = post_id;
        this.sent_by = sent_by;
        this.sent_to = sent_to;
        this.timestamp = timestamp;
    }

    //    GETTERS

    public int getSeen() {
        return seen;
    }

    public int getType() {
        return type;
    }

    public String getPost_id() {
        return post_id;
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


    public void setSeen(int seen) {
        this.seen = seen;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
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
