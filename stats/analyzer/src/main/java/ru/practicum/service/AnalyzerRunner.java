package ru.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnalyzerRunner implements CommandLineRunner {
    final UserActionProcessor userActionProcessor;
    final SimilarityProcessor similarityProcessor;

    @Override
    public void run(String... args) {
        Thread userActionThread = new Thread(userActionProcessor);
        userActionThread.setName("UserActionHandlerThread");
        userActionThread.start();
        similarityProcessor.start();
    }
}