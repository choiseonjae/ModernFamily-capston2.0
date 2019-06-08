package com.example.capstonee.Model;

public class ImageUpload {
    private String name;
    private String url;
    private String family;

    public ImageUpload(){}
    public ImageUpload(String name, String url, String family) {
        this.name = name;
        this.url = url;
        this.family = family;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getName() {
        return name;
    }

    public String getUrl() { return url; }

    public String getFamily() {
        return family;
    }
}
