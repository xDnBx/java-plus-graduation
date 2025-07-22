package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class RequestApp {
    public static void main(String[] args) {
        SpringApplication.run(RequestApp.class, args);
    }
}