package com.tattoos.clientapp.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Tattoo {
    public String title;
    public String bodyPart;
    public String style;
    public String artist;
    public String artistName;
    public String url;


    public Tattoo(){}

    public Tattoo(String title,String bodyPart,String style,String artist,String url,String artistName){
        this.title=title;
        this.bodyPart=bodyPart;
        this.style=style;
        this.artist=artist;
        this.url=url;
        this.artistName = artistName;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title",title);
        result.put("bodyPart", bodyPart);
        result.put("style",style);
        result.put("artist",artist);
        result.put("artistName",artistName);
        result.put("url",url);
        return result;
    }
}
