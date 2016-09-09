package com.tattoos.clientapp;

import android.app.Application;
import android.graphics.Bitmap;

import com.tattoos.clientapp.adapters.GridItem;

import java.util.ArrayList;

public class MyApplicationContext extends Application{

    private String _username;
    private String _photoURL;
    private String _email;
    private String _lastKnownLocation;

    private Bitmap _artistAvatar;
    private String _artistBio;
    private String _artistName;

    private ArrayList<GridItem> gridItemCache;

    @Override
    public void onCreate() {
        super.onCreate();
        _username = "anonym";
        _photoURL = "";
        _email = "";
        _lastKnownLocation = "";
        _artistBio = "";
        _artistName = "";
        gridItemCache = new ArrayList<GridItem>();
    }

    public String getUsername() {
        return _username;
    }

    public void setUsername(String username) {
        this._username = username;
    }

    public String getPhotoURL() {
        return _photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this._photoURL = photoURL;
    }

    public String getEmail() {
        return _email;
    }

    public void setEmail(String email) {
        this._email = email;
    }

    public ArrayList<GridItem> getGridItemCache() {
        return gridItemCache;
    }

    public void setGridItemCache(ArrayList<GridItem> gridItemCache) {
        this.gridItemCache = gridItemCache;
    }

    public String getLastKnownLocation() {
        return _lastKnownLocation;
    }

    public void setLastKnownLocation(String lastKnownLocation) {
        this._lastKnownLocation = lastKnownLocation;
    }

    public Bitmap getArtistAvatar() {
        return _artistAvatar;
    }

    public void setArtistAvatar(Bitmap artistAvatar) {
        this._artistAvatar = artistAvatar;
    }

    public String getArtistBio() {
        return _artistBio;
    }

    public void setArtistBio(String artistBio) {
        this._artistBio = artistBio;
    }

    public String getArtistName() {
        return _artistName;
    }

    public void setArtistName(String artistName) {
        this._artistName = artistName;
    }

}
