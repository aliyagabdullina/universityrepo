package org.example.data;


import javax.persistence.*;
import java.sql.Timestamp;

@Entity
public class ChatHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer universityId;
    private String message;
    private String response;

    @Column(name = "timestamp", nullable = false, updatable = false)
    private Timestamp timestamp;

    // Конструктор без параметров (нужен для Hibernate)
    public ChatHistory() {}

    // Конструктор с параметрами
    public ChatHistory(Integer universityId, String message, String response) {
        this.universityId = universityId;
        this.message = message;
        this.response = response;
        this.timestamp = new Timestamp(System.currentTimeMillis()); // текущий момент времени
    }

    // Геттеры и сеттеры

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getUniversityId() {
        return universityId;
    }

    public void setUniversityId(Integer universityId) {
        this.universityId = universityId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}

