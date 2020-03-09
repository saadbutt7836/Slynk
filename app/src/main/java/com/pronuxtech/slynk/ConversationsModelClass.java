package com.pronuxtech.slynk;

import java.util.List;

public class ConversationsModelClass {
    private String chatroom_id, cid, last_message, sent_by, sent_to, sent_to_name;
    private Double timestamp;
    private int type, unread_counter;
    private List<String> members;


    public ConversationsModelClass() {

    }

    public ConversationsModelClass(String chatroom_id, String cid, String last_message, String sent_by, String sent_to,
                                   String sent_to_name, Double timestamp, int type, int unread_counter,
                                   List<String> members) {
        this.chatroom_id = chatroom_id;
        this.cid = cid;
        this.last_message = last_message;
        this.sent_by = sent_by;
        this.sent_to = sent_to;
        this.sent_to_name = sent_to_name;
        this.timestamp = timestamp;
        this.type = type;
        this.unread_counter = unread_counter;
        this.members = members;
    }

//    GETTERS

    public String getChatroom_id() {
        return chatroom_id;
    }

    public String getCid() {
        return cid;
    }

    public String getLast_message() {
        return last_message;
    }

    public String getSent_by() {
        return sent_by;
    }

    public String getSent_to() {
        return sent_to;
    }

    public String getSent_to_name() {
        return sent_to_name;
    }

    public Double getTimestamp() {
        return timestamp;
    }

    public int getType() {
        return type;
    }

    public int getUnread_counter() {
        return unread_counter;
    }

    public List<String> getMembers() {
        return members;
    }


//    SETTERS


    public void setChatroom_id(String chatroom_id) {
        this.chatroom_id = chatroom_id;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public void setLast_message(String last_message) {
        this.last_message = last_message;
    }

    public void setSent_by(String sent_by) {
        this.sent_by = sent_by;
    }

    public void setSent_to(String sent_to) {
        this.sent_to = sent_to;
    }

    public void setSent_to_name(String sent_to_name) {
        this.sent_to_name = sent_to_name;
    }

    public void setTimestamp(Double timestamp) {
        this.timestamp = timestamp;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setUnread_counter(int unread_counter) {
        this.unread_counter = unread_counter;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }
}
