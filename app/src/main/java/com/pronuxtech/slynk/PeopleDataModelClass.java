package com.pronuxtech.slynk;

public class PeopleDataModelClass {

    private int id;
    private String username, userMessages, messageTime, messagesCount;
    private int userProfile;

    public PeopleDataModelClass() {
    }

    public PeopleDataModelClass(int id, String username, String userMessages, String messageTime, String messagesCount, int userProfile) {
        this.id = id;
        this.username = username;
        this.userMessages = userMessages;
        this.messageTime = messageTime;
        this.messagesCount = messagesCount;
        this.userProfile = userProfile;
    }

    //getter
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getUserMessages() {
        return userMessages;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public String getMessagesCount() {
        return messagesCount;
    }

    public int getUserProfile() {
        return userProfile;
    }

//    setter

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserMessages(String userMessages) {
        this.userMessages = userMessages;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public void setMessagesCount(String messagesCount) {
        this.messagesCount = messagesCount;
    }

    public void setUserProfile(int userProfile) {
        this.userProfile = userProfile;
    }
}
