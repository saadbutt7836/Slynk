package com.pronuxtech.slynk;

public class PostCommentsClass {
    String body, posted_by;
    double timestamp;

    public PostCommentsClass() {

    }

    public PostCommentsClass(String body, String posted_by, double timestamp) {
        this.body = body;
        this.posted_by = posted_by;
        this.timestamp = timestamp;
    }

    public String getBody() {
        return body;
    }

    public String getPosted_by() {
        return posted_by;
    }

    public double getTimestamp() {
        return timestamp;
    }
}
