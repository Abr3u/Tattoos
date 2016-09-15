package com.tattoos.clientapp;

import android.app.Application;
import android.graphics.Bitmap;

import com.google.firebase.auth.FirebaseUser;
import com.tattoos.clientapp.adapters.GridItem;

import java.util.ArrayList;

public class MyApplicationContext extends Application{

    private String _username;
    private String _photoURL;
    private String _email;

    private FirebaseUser firebaseUser;

    @Override
    public void onCreate() {
        super.onCreate();
        _username = "anonym";
        _photoURL = "";
        _email = "";
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

    public void setFirebaseUser(FirebaseUser firebaseUser) {
        this.firebaseUser = firebaseUser;
    }

    public FirebaseUser getFirebaseUser(){
        return this.firebaseUser;
    }
}
