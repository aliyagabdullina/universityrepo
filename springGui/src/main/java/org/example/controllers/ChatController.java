package org.example.controllers;

import org.example.data.ChatHistory;
import org.example.data.ChatMessage;
import org.example.data.ChatRequest;
import org.example.data.ChatResponse;
import org.example.repositories.ChatHistoryRepository;
import org.example.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatHistoryRepository chatHistoryRepository;

    @PostMapping
    public Map<String, String> sendMessage(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        int universityId = 1;  // Получить университет ID из сессии

        // Получаем ответ от LLM API
        String response = getLLMResponse(message);

        // Сохраняем историю в БД
        ChatHistory chatHistory = new ChatHistory(universityId, message, response);
        chatHistoryRepository.save(chatHistory);

        return Map.of("reply", response);
    }

    @GetMapping("/history")
    public List<ChatHistory> getChatHistory() {
        // Получаем историю чатов для текущего университета
        int universityId = 1; // Замените на реальный ID университета из сессии
        return chatHistoryRepository.findByUniversityId(universityId);
    }

    private String getLLMResponse(String userMessage) {
        // Ваш код для обращения к LLM API
        return "Ответ на сообщение: " + userMessage;
    }
}
