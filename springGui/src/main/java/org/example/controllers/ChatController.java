package org.example.controllers;

import org.example.data.ChatHistory;
import org.example.data.ChatMessage;
import org.example.data.ChatRequest;
import org.example.data.ChatResponse;
import org.example.repositories.ChatHistoryRepository;
import org.example.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatHistoryRepository chatHistoryRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    //private final String pythonApiUrl = "http://localhost:5001/llm"; // адрес Flask-сервера
    private final String pythonApiUrl = "http://192.168.0.127:5001/llm";


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

    @PostMapping("/upload")
    public Map<String, String> handleMessage(
            @RequestParam(value = "message", required = false) String message,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        System.out.println("I am here");
        int universityId = 1;
        if (file != null && !file.isEmpty()) {
            System.out.println("Uploaded file: " + file.getOriginalFilename());
            // Обработка любого файла — не только CSV
        }

        String response = getLLMResponse(message);

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
        String url = "http://localhost:5001/llm"; // Или IP машины

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Тело запроса
        Map<String, String> payload = new HashMap<>();
        payload.put("message", userMessage);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            if (response.getBody() != null && response.getBody().get("reply") != null) {
                return response.getBody().get("reply").toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Ошибка при вызове LLM";
    }


}
