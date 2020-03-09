package com.pronuxtech.slynk;

public class TextMessagesModelClass {
    String message, message_id, sender, sender_name, audio, image, thumbnail, video;
    Double timestamp;
    int status, type;

    public TextMessagesModelClass() {

    }

    //    CONSTRUCTOR

    public TextMessagesModelClass(String message, String message_id, String sender, String sender_name, String audio, String image, String thumbnail, String video, Double timestamp, int status, int type) {
        this.message = message;
        this.message_id = message_id;
        this.sender = sender;
        this.sender_name = sender_name;
        this.audio = audio;
        this.image = image;
        this.thumbnail = thumbnail;
        this.video = video;
        this.timestamp = timestamp;
        this.status = status;
        this.type = type;
    }


    //    GETTERS

    public String getMessage() {
        return message;
    }

    public String getMessage_id() {
        return message_id;
    }

    public String getSender() {
        return sender;
    }

    public String getSender_name() {
        return sender_name;
    }

    public String getAudio() {
        return audio;
    }

    public String getImage() {
        return image;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getVideo() {
        return video;
    }

    public Double getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public int getType() {
        return type;
    }


    //    SETTERS


    public void setMessage(String message) {
        this.message = message;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public void setTimestamp(Double timestamp) {
        this.timestamp = timestamp;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setType(int type) {
        this.type = type;
    }
}
