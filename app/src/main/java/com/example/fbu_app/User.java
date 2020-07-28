package com.example.fbu_app;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

@ParseClassName("Post")
public class User extends ParseUser {

    public static final String KEY_USERNAME = "username";
    public static final String KEY_PROFILE_IMAGE = "profileImage";
    public static final String KEY_NAME="name";
    public static final String KEY_LIKES="likedPosts";

    public String getUsername() {
        return getString(KEY_USERNAME);
    }

    public ParseFile getProfileImage() {
        return getParseFile(KEY_PROFILE_IMAGE);
    }

    public void setName(String name) { put(KEY_NAME, name);}

    public List<Object> getLikedPosts() { return getList(KEY_LIKES); }

    public void addLikedPost(Post post) {
        List<Object> likedPosts = getLikedPosts();
        likedPosts.add((Object) post);
        put(KEY_LIKES, likedPosts);
    }

    public void deleteLikedPost(Post post) {
        List<Object> likedPosts = getLikedPosts();
        likedPosts.remove((Object) post);
        put(KEY_LIKES, likedPosts);
    }
}
