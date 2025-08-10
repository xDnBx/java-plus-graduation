package ru.practicum.handler;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.model.EventSimilarity;
import ru.practicum.repository.SimilarityRepository;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SimilarityHandler {
    final SimilarityRepository similarityRepository;

    public void handle(EventSimilarityAvro avro) {
        log.info("Сохранение схожести события: {}", avro);
        EventSimilarity similarity = EventSimilarity.builder()
                .eventA(avro.getEventA())
                .eventB(avro.getEventB())
                .score(avro.getScore())
                .timestamp(avro.getTimestamp())
                .build();
        similarityRepository.save(similarity);
    }
}