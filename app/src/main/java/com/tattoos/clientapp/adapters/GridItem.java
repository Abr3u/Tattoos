package com.tattoos.clientapp.adapters;

public class GridItem {
    private byte[] tattoo_bytes;
    private String tattoo_title;
    private String tattoo_artist;
    private String tattoo_style;
    private String tatto_body_part;

    private String artist_name;
    private String artist_bio;
    private byte[] artist_avatar;

    public GridItem() {
        super();
    }

    public byte[] getTattoo_bytes() {
        return tattoo_bytes;
    }

    public void setTattoo_bytes(byte[] tattoo_bytes) {
        this.tattoo_bytes = tattoo_bytes;
    }

    public String getTattoo_title() {
        return tattoo_title;
    }

    public void setTattoo_title(String tattoo_title) {
        this.tattoo_title = tattoo_title;
    }

    public String getTattoo_artist() {
        return tattoo_artist;
    }

    public void setTattoo_artist(String tattoo_artist) {
        this.tattoo_artist = tattoo_artist;
    }

    public String getTattoo_style() {
        return tattoo_style;
    }

    public void setTattoo_style(String tattoo_style) {
        this.tattoo_style = tattoo_style;
    }

    public String getTatto_body_part() {
        return tatto_body_part;
    }

    public void setTatto_body_part(String tatto_body_part) {
        this.tatto_body_part = tatto_body_part;
    }

    public String getArtist_name() {
        return artist_name;
    }

    public void setArtist_name(String artist_name) {
        this.artist_name = artist_name;
    }

    public String getArtist_bio() {
        return artist_bio;
    }

    public void setArtist_bio(String artist_bio) {
        this.artist_bio = artist_bio;
    }

    public byte[] getArtist_avatar() {
        return artist_avatar;
    }

    public void setArtist_avatar(byte[] artist_avatar) {
        this.artist_avatar = artist_avatar;
    }

}
