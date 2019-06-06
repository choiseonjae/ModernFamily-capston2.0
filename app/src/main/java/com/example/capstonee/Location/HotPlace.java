package com.example.capstonee.Location;

import java.util.ArrayList;

public class HotPlace {

    // 사진 제목, 주소, 사진들
    private String title, address;
    private ArrayList<String> imageList = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ArrayList<String> getImageList() {
        return imageList;
    }

    public void setImageList(ArrayList<String> imageList) {
        this.imageList = imageList;
    }
}
