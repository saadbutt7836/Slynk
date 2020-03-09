package com.pronuxtech.slynk;

public class StoryDataHomePage {
    int id;
    int storyImages;
    String storyUserName;

    public StoryDataHomePage(int id, int storyImages, String storyUserName) {
        this.id = id;
        this.storyImages = storyImages;
        this.storyUserName = storyUserName;
    }

//    STORY GETTER

    public int getId() {
        return id;
    }

    public int getStoryImages() {
        return storyImages;
    }

    public String getStoryUserName() {
        return storyUserName;
    }

//    STORY SETTER

    public void setId(int id) {
        this.id = id;
    }

    public void setStoryImages(int storyImages) {
        this.storyImages = storyImages;
    }

    public void setStoryUserName(String storyUserName) {
        this.storyUserName = storyUserName;
    }

}
