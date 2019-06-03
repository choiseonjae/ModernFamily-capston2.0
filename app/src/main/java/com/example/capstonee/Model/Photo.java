package com.example.capstonee.Model;

public class Photo {
    private String role;
    private String uri;

    public Photo(){}
    public Photo(String uri){
        this.uri = uri;
    }
    public Photo(String role, String uri) {
        this.role = role;
        this.uri = uri;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
