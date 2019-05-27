package com.example.capstonee.Model;

public class Chatting {
    private String Name;
    private String Phone;
    private int Photo;

    public Chatting() {
    }

    public Chatting(String name, String phone, int Photo) {
        Name = name;
        Phone = phone;
        this.Photo = Photo;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public int getPhoto() {
        return Photo;
    }

    public void setPhoto(int photo) {
        Photo = photo;
    }
}
