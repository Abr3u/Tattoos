package com.tattoos.clientapp.models;

import android.util.Log;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.storage.StreamDownloadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


@IgnoreExtraProperties
public class Artist {
    public String username;
    public String locality;
    public String bio;
    public ArrayList<String> tattoosUrl;

    public Artist() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Artist(String username,String bio, String locality, ArrayList<String> tattoosUrl) {
        this.username = username;
        this.bio = bio;
        this.locality=locality;
        this.tattoosUrl = tattoosUrl;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("username",username);
        result.put("bio", bio);
        result.put("locality",locality);

        HashMap<String, Object> tattoos = new HashMap<>();
        int i;
        for(i=0;i<tattoosUrl.size();i++){
            tattoos.put("t"+i,tattoosUrl.get(i));
        }
        result.put("tattoos",tattoos);
        return result;
    }
}
