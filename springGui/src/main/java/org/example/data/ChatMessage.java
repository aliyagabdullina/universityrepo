package org.example.data;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ChatMessage {
    private String sender;
    private String message;

    @JsonCreator
    public ChatMessage(@JsonProperty("sender") String sender, @JsonProperty("message") String message) {
        this.sender = sender;
        this.message = message;
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

}
