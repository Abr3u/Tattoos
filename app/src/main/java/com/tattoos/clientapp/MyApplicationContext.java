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
    private String _artistBio;
    private String _artistName;

    private ArrayList<GridItem> tattoosCache;
    private ArrayList<GridItem> artistsCache;
    private ArrayList<Bitmap> artistTattoos;

    @Override
    public void onCreate() {
        super.onCreate();
        _username = "anonym";
        _photoURL = "";
        _email = "";
        _lastKnownLocation = "";
        _artistBio = "";
        _artistName = "";
        tattoosCache = new ArrayList<GridItem>();
        artistsCache = new ArrayList<GridItem>();
        artistTattoos = new ArrayList<Bitmap>();
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

    public ArrayList<GridItem> getTattoosCache() {
        return tattoosCache;
    }

    public void setTattoosCache(ArrayList<GridItem> tattoosCache) {
        this.tattoosCache = tattoosCache;
    }

    public ArrayList<GridItem> getArtistsCache() {
        return artistsCache;
    }

    public void setArtistsCache(ArrayList<GridItem> artistsCache) {
        this.artistsCache = artistsCache;
    }

    public String getLastKnownLocation() {
        return _lastKnownLocation;
    }

    public void setLastKnownLocation(String lastKnownLocation) {
        this._lastKnownLocation = lastKnownLocation;
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

    public ArrayList<Bitmap> getArtistTattoos() {
        return artistTattoos;
    }

    public void setArtistTattoos(ArrayList<Bitmap> artistTattoos) {
        this.artistTattoos = artistTattoos;
    }

}
