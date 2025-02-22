package org.example;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringApp {
    public static void main(String[] args) {
        DataBaseInteractor dataBaseInteractor = new DataBaseInteractorImpl();
        SpringApplication.run(SpringApp.class, args);
    }
}