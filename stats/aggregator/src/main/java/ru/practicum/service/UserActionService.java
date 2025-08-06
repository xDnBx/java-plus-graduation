package ru.practicum.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.time.Instant;
import java.util.*;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserActionService {
    final Map<Long, Map<Long, Double>> eventActions = new HashMap<>();
    final Map<Long, Double> eventWeightSums = new HashMap<>();
    final Map<Long, Map<Long, Double>> eventMinWeightSums = new HashMap<>();
    @Value("${user-action.view}")
    Double viewAction;
    @Value("${user-action.register}")
    Double registerAction;
    @Value("${user-action.like}")
    Double likeAction;

    public List<EventSimilarityAvro> updateSimilarity(UserActionAvro avro) {
        long eventId = avro.getEventId();
        updateUserAction(avro);

        List<EventSimilarityAvro> result = new ArrayList<>();
        for (long anotherEventId : eventActions.keySet()) {
            if (eventId != anotherEventId) {
                Double similarity = calculateSimilarity(eventId, anotherEventId);
                result.add(createAvro(eventId, anotherEventId, similarity, avro.getTimestamp()));
            }
        }
        return result;
    }

    private void updateUserAction(UserActionAvro avro) {
        Long userId = avro.getUserId();
        Long eventId = avro.getEventId();
        Map<Long, Double> userActions = eventActions.computeIfAbsent(eventId, v -> new HashMap<>());
        log.info("Получили действия пользователя с id = {} на событие = {}: {}", userId, eventId, userActions);
        Double oldWeight = userActions.getOrDefault(userId, 0.0);
        Double newWeight = toWeight(avro.getActionType());

        if (oldWeight == null || oldWeight < newWeight) {
            userActions.put(userId, newWeight);
            eventActions.put(eventId, userActions);

            Double eventChange = oldWeight == null ? newWeight : newWeight - oldWeight;
            eventWeightSums.merge(eventId, eventChange, Double::sum);
            log.info("Обновили вес для события с id = {}: {}", eventId, eventWeightSums.get(eventId));

            updateMinWeightSums(userId, eventId, oldWeight, newWeight);
            log.info("Обновили минимальные веса для события с id = {}: {}", eventId, eventMinWeightSums);
        } else {
            log.info("Вес не изменился для события с id = {}", eventId);
        }
    }

    private Double toWeight(ActionTypeAvro actionType) {
        return switch (actionType) {
            case VIEW -> viewAction;
            case REGISTER -> registerAction;
            case LIKE -> likeAction;
        };
    }

    private void updateMinWeightSums(Long userId, Long eventId, Double oldWeight, Double newWeight) {
        for (Map.Entry<Long, Map<Long, Double>> entry : eventActions.entrySet()) {
            Long anotherEventId = entry.getKey();
            if (eventId.equals(anotherEventId)) continue;

            Double anotherWeight = entry.getValue().getOrDefault(userId, 0.0);
            if (anotherWeight > 0) {
                Double oldMinSum = eventMinWeightSums
                        .computeIfAbsent(eventId, v -> new HashMap<>())
                        .getOrDefault(anotherEventId, 0.0);

                Double newMinSum = oldMinSum - Math.min(oldWeight, anotherWeight) + Math.min(newWeight, anotherWeight);

                eventMinWeightSums.get(eventId).put(anotherEventId, newMinSum);
            }
        }
    }

    private Double calculateSimilarity(Long eventId, Long anotherEventId) {
        Double sumMin = eventMinWeightSums
                .getOrDefault(eventId, new HashMap<>())
                .getOrDefault(anotherEventId, 0.0);

        Double sumEvent = eventWeightSums.getOrDefault(eventId, 0.0);
        Double sumAnotherEvent = eventWeightSums.getOrDefault(anotherEventId, 0.0);

        if (sumEvent == 0 || sumAnotherEvent == 0) {
            return 0.0;
        }

        return sumMin / (Math.sqrt(sumEvent) * Math.sqrt(sumAnotherEvent));
    }

    private EventSimilarityAvro createAvro(Long eventId, Long anotherEventId, Double similarity, Instant timestamp) {
        long eventA = Math.min(eventId, anotherEventId);
        long eventB = Math.max(eventId, anotherEventId);

        return EventSimilarityAvro.newBuilder()
                .setEventA(eventA)
                .setEventB(eventB)
                .setScore(similarity)
                .setTimestamp(timestamp)
                .build();
    }
}