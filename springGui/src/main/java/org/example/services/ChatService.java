package org.example.services;

import org.example.data.ChatMessage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatService {

    private final List<ChatMessage> chatHistory = new ArrayList<>();

    // Get AI reply
    public String getReply(String message) {
        // AI logic goes here (for now, return a static response)
        String reply = "This is a response to: " + message;

        // Save message and reply
        chatHistory.add(new ChatMessage("You", message));
        chatHistory.add(new ChatMessage("AI", reply));

        return reply;
    }

    // Get chat history
    public List<ChatMessage> getChatHistory() {
        return chatHistory;
    }
}
