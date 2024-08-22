package org.example.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Message {
    @JsonProperty("sender")
    public String sender;
    @JsonProperty("receiver")
    public String receiver;
    @JsonProperty("message")
    public String message;
    @JsonProperty("group")
    public String group;
    @JsonProperty("ips")
    public List<String> ips;

    public Message() {
    }

    public Message(String sender, String message) {
        this.sender = sender;
        this.message = message.trim();
    }

    public Message(String sender, String receiver, String message) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }

    public Message(String sender, String receiver, String message, String group) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.group = group;
    }

    public Message(String sender, String receiver, String message, String group, List<String> ips) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.group = group;
        this.ips = ips;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Message{" +
                "nickname='" + sender + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
