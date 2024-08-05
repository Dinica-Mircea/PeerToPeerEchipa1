package org.example;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Message {
    @JsonProperty("nickname")
    public String nickname;
    @JsonProperty("message")
    public String message;

    public Message() {
    }

    public Message(String nickname, String message) {
        this.nickname = nickname;
        this.message = message.trim();
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Message{" +
                "nickname='" + nickname + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
