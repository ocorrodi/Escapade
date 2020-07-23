package com.example.fbu_app;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseUser;

@ParseClassName("Post")
public class User extends ParseUser {

    public static final String KEY_USERNAME = "username";
    public static final String KEY_PROFILE_IMAGE = "profileImage";
    public static final String KEY_NAME="name";

    public String getUsername() {
        return getString(KEY_USERNAME);
    }

    public ParseFile getProfileImage() {
        return getParseFile(KEY_PROFILE_IMAGE);
    }

    public void setName(String name) { put(KEY_NAME, name);}
}
