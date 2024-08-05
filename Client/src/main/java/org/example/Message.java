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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Message{" +
                "nickname='" + nickname + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
