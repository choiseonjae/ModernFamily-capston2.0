package com.example.capstonee.Model;

import java.util.HashMap;
import java.util.Map;

public class Chat {

    private String sender, message, uri, time;
    private Map<String, Integer> reader;

    public Chat() {
        sender = message = uri = time = "";
        reader = new HashMap<>();
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

    public Map<String, Integer> getReader() {
        return reader;
    }

    public void setReader(Map<String, Integer> reader) {
        this.reader = reader;
    }
}

