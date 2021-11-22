package com.example.jily.model;

import com.example.jily.utility.Parser;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Profile {

    @SerializedName("user")
    @Expose
    private Integer user;

    @SerializedName("songs")
    @Expose
    private String songs;

    @SerializedName("transcriptions")
    @Expose
    private String transcriptions;

    public Profile(Integer user, String songs, String transcriptions) {
        this.user = user;
        this.songs = songs;
        this.transcriptions = transcriptions;
    }

    public Integer getUser() { return user; }

    public void setUser(Integer user) { this.user = user; }

    public ArrayList<String> getSongs() { return Parser.getAsList(songs); }

    public void setSongs(ArrayList<String> songs) { this.songs = Parser.setToString(songs); }

    public ArrayList<String> getTranscriptions() { return Parser.getAsList(transcriptions); }

    public void setTranscriptions(ArrayList<String> transcriptions) {
        this.transcriptions = Parser.setToString(transcriptions);
    }
}
