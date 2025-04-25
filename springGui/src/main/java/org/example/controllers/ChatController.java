package org.example.controllers;

import org.example.data.ChatMessage;
import org.example.data.ChatRequest;
import org.example.data.ChatResponse;
import org.example.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    // Endpoint to send a message
    @PostMapping
    public ResponseEntity<ChatResponse> sendMessage(@RequestBody ChatRequest request) {
        String reply = chatService.getReply(request.getMessage());
        return ResponseEntity.ok(new ChatResponse(reply));
    }

    // Endpoint to get chat history
    @GetMapping("/history")
    public List<ChatMessage> getChatHistory() {
        return chatService.getChatHistory();
    }
}

