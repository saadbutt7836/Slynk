package com.pronuxtech.slynk;

import java.util.HashMap;
import java.util.Map;

public class PostDataHomePage {

    public double timestamp;
    public int comments;
    public int type;
    public String post_image;
    public String posted_by;
    public String status;
    public String pid;
    public int likesCount = 0;
    public Map<String, Boolean> likes = new HashMap<>();


    public PostDataHomePage() {

    }

    public PostDataHomePage(double timestamp, int comments, int type, String post_image, String posted_by,
                            String status, String pid, int likesCount) {
        this.timestamp = timestamp;
        this.comments = comments;
        this.type = type;
        this.post_image = post_image;
        this.posted_by = posted_by;
        this.status = status;
        this.pid = pid;
        this.likesCount = likesCount;
    }


    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("pid", pid);
        result.put("timestamp", timestamp);
        result.put("comments", comments);
        result.put("type", type);
        result.put("post_image", post_image);
        result.put("posted_by", posted_by);
        result.put("status", status);
        result.put("likesCount", likesCount);
        result.put("likes", likes);

        return result;
    }


//    GETTERS


    public double getTimestamp() {
        return timestamp;
    }

    public int getComments() {
        return comments;
    }

    public int getType() {
        return type;
    }

    public String getPost_image() {
        return post_image;
    }

    public String getPosted_by() {
        return posted_by;
    }

    public String getStatus() {
        return status;
    }

    public String getPid() {
        return pid;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public Map<String, Boolean> getLikes() {
        return likes;
    }


//    SETTERS

    public void setTimestamp(double timestamp) {
        this.timestamp = timestamp;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setPost_image(String post_image) {
        this.post_image = post_image;
    }

    public void setPosted_by(String posted_by) {
        this.posted_by = posted_by;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public void setLikes(Map<String, Boolean> likes) {
        this.likes = likes;
    }


//
//    public void setTimestamp(double timestamp) {
//        this.timestamp = timestamp;
//    }
//
//    public void setComments(int comments) {
//        this.comments = comments;
//    }
//
//    public void setType(int type) {
//        this.type = type;
//    }
//
//    public void setPost_image(String post_image) {
//        this.post_image = post_image;
//    }
//
//    public void setPosted_by(String posted_by) {
//        this.posted_by = posted_by;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//    }
//
//    public void setUid(String uid) {
//        this.pid = uid;
//    }
//
//    public void setLikesCount(int likesCount) {
//        this.likesCount = likesCount;
//    }
//
//    public void setLikes(Map<String, Boolean> likes) {
//        this.likes = likes;
//    }


}
