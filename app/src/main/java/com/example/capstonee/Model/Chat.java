package com.example.capstonee.Model;

public class Chat {

    private String sender, message , uri , time;
    private int noRead;

    public Chat(){
        sender = message = uri = time = "";
        noRead = 0;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getNoRead() {
        return noRead;
    }

    public void setNoRead(int noRead) {
        this.noRead = noRead;
    }
}

