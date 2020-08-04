package com.example.fbu_app;

import android.media.Image;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import org.parceler.Parcel;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ParseClassName("Post")
public class Post extends ParseObject {

    public static final String KEY_TITLE = "title";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_USER = "user";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_RATING = "rating";
    public static final String KEY_NOTES = "notes";
    public static final String KEY_DATE = "date";
    public static final String KEY_IMAGES = "images";
    public static final String KEY_TAGS = "tags";
    public static final String KEY_PLACE = "place";
    public static final String KEY_LIKES = "likes";
    public static final String KEY_COUNTRY = "country";
    public static final String KEY_USER_NAME = "userName";

    public String getTitle() {
        return getString(KEY_TITLE);
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint(KEY_LOCATION);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public int getRating() {
        return getInt(KEY_RATING);
    }

    public String getNotes() {
        return getString(KEY_NOTES);
    }

    public Date getDate() {
        return getDate(KEY_DATE);
    }

    public String getUserName() { return getString(KEY_USER_NAME); }

    public List<ParseFile> getImages() {
        return getList(KEY_IMAGES);
    }

    public List<String> getTags() {
        return getList(KEY_TAGS);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public void setUserName(String name) { put(KEY_USER_NAME, name); }

    public void setTitle(String title) {
        put(KEY_TITLE, title);
    }

    public void setLocation(ParseGeoPoint location) {
        put(KEY_LOCATION, location);
    }

    public void setDate(Date date) {
        put(KEY_DATE, date);
    }

    public void setCountry(String country) { put(KEY_COUNTRY, country); }

    public void setImages(List<ParseFile> images) {
        put(KEY_IMAGES, images);
    }

    public void setTags(List<String> tags) {
        put(KEY_TAGS, tags);
    }

    public void setRating(int rating) {
        put(KEY_RATING, rating);
    }

    public void setNotes(String notes) {
        put(KEY_NOTES, notes);
    }

    public void setPlace(String place) { put(KEY_PLACE, place); }

    public String getPlace() { return getString(KEY_PLACE); }

    public String getCountry() { return getString(KEY_COUNTRY); }

    public int getLikes() { return getInt(KEY_LIKES); }

    public void addLike() {
        put(this.KEY_LIKES, getLikes() + 1);
        saveInBackground();
    }

    public void removeLike() {
        put(this.KEY_LIKES, getLikes() - 1);
        saveInBackground();
    }

    public void setLikes(int likes) { put(KEY_LIKES, likes); }

}