package com.example.fbu_app;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

@ParseClassName("ParseUser")
public class User extends ParseUser {

    public static final String KEY_USERNAME = "username";
    public static final String KEY_PROFILE_IMAGE = "profileImage";
    public static final String KEY_NAME="name";
    public static final String KEY_LIKES="likedPosts";
    public static ParseUser user;

    public User(ParseUser user) {
        this.user = user;
    }

    public String getUsername() {
        return user.getString(KEY_USERNAME);
    }

    public ParseFile getProfileImage() {
        return user.getParseFile(KEY_PROFILE_IMAGE);
    }

    public void setName(String name) { user.put(KEY_NAME, name);}

    public List<Object> getLikedPosts() { return user.getList(KEY_LIKES); }

    public void addLikedPost(Post post) {
        List<Object> likedPosts = getLikedPosts();
        likedPosts.add((Object) post);
        super.put(KEY_LIKES, likedPosts);
    }

    public void deleteLikedPost(Post post) {
        List<Object> likedPosts = getLikedPosts();
        likedPosts.remove((Object) post);
        super.put(KEY_LIKES, likedPosts);
    }

    public void setLikedPosts(List<Object> posts) { super.put(KEY_LIKES, posts);}
}
