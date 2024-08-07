package domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Message {
    @JsonProperty("sender")
    public String sender;
    @JsonProperty("receiver")
    public String receiver;
    @JsonProperty("message")
    public String message;
    @JsonProperty("group")
    public String group;

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
