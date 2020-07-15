package com.example.fbu_app;

import android.media.Image;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import org.parceler.Parcel;

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

    public List<Image> getImages() {
        return getList(KEY_IMAGES);
    }

    public List<String> getTags() {
        return getList(KEY_TAGS);
    }

    public void setTitle(String title) {
        put(KEY_TITLE, title);
    }

    public void setLocation(ParseGeoPoint location) {
        put(KEY_LOCATION, location);
    }

    public void setDate(Date date) {
        put(KEY_DATE, date);
    }

    public void setImages(List<Image> images) {
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

}