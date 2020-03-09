package com.pronuxtech.slynk;

import java.util.HashMap;
import java.util.Map;

public class MyFriendsModelClass {

    public Map<String, Boolean> friends = new HashMap<>();

    public MyFriendsModelClass() {

    }

//    CONSTRUCTOR


    public MyFriendsModelClass(Map<String, Boolean> friends) {
        this.friends = friends;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();


        result.put("friends", friends);

        return result;
    }


    public Map<String, Boolean> getFriends() {
        return friends;
    }

    public void setFriends(Map<String, Boolean> friends) {
        this.friends = friends;
    }
}
