package com.example.capstonee.Model;

public class Image {
    private int Thumbnail;

    public Image(){}

    public Image(int thumbnail) {
        Thumbnail = thumbnail;
    }

    public int getThumbnail() {
        return Thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        Thumbnail = thumbnail;
    }
}
