package ru.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserActionService {
    final Map<Long, Map<Long, Double>> eventActions = new HashMap<>();
    final Map<Long, Double> eventWeightSums = new HashMap<>();
    final Map<Long, Map<Long, Double>> eventMinWeightSums = new HashMap<>();

    @Value("${user-action.view:0.4}")
    Double viewAction;

    @Value("${user-action.register:0.8}")
    Double registerAction;

    @Value("${user-action.like:1.0}")
    Double likeAction;

    public List<EventSimilarityAvro> updateSimilarity(UserActionAvro avro) {
        long userId = avro.getUserId();
        long eventId = avro.getEventId();

        Map<Long, Double> userActions = eventActions.computeIfAbsent(eventId, v -> new HashMap<>());
        log.info("Получили действия пользователя с id = {} на событие = {}: {}", userId, eventId, userActions);

        double oldWeight = userActions.getOrDefault(userId, 0.0);
        double newWeight = toWeight(avro.getActionType());

        List<EventSimilarityAvro> result = new ArrayList<>();
        if (newWeight > oldWeight) {
            updateUserAction(userId, eventId, oldWeight, newWeight, userActions);
        } else {
            log.info("Вес не изменился для события с id = {}", eventId);
            return result;
        }

        for (Map.Entry<Long, Map<Long, Double>> entry : eventActions.entrySet()) {
            long anotherEventId = entry.getKey();
            if (eventId == anotherEventId) continue;

            double anotherWeight = entry.getValue().getOrDefault(userId, 0.0);
            if (anotherWeight > 0) {
                double newMinSum = updateMinWeightSums(userId, eventId, anotherEventId, oldWeight, newWeight);
                double similarity = calculateSimilarity(eventId, anotherEventId, newMinSum);
                result.add(createAvro(eventId, anotherEventId, similarity, avro.getTimestamp()));
            }
        }

        return result;
    }

    private Double toWeight(ActionTypeAvro actionType) {
        return switch (actionType) {
            case VIEW -> viewAction;
            case REGISTER -> registerAction;
            case LIKE -> likeAction;
        };
    }

    private void updateUserAction(long userId, long eventId, double oldWeight, double newWeight,
                                  Map<Long, Double> userActions) {
        double oldSum = eventWeightSums.getOrDefault(eventId, 0.0);
        double newSum = oldSum - oldWeight + newWeight;

        userActions.put(userId, newWeight);
        eventWeightSums.put(eventId, newSum);
        log.info("Обновили вес для события с id = {}: {}", eventId, eventWeightSums.get(eventId));
    }

    private Double updateMinWeightSums(long userId, long eventId, long anotherEventId, double oldWeight,
                                       double newWeight) {
        log.info("Обновляем минимальные веса для событий с id = {} и {}", eventId, anotherEventId);
        long newEventId = Math.min(eventId, anotherEventId);
        long newAnotherEventId = Math.max(eventId, anotherEventId);

        double oldAnotherWeight = eventActions
                .getOrDefault(anotherEventId, new HashMap<>())
                .getOrDefault(userId, 0.0);

        if (oldAnotherWeight == 0.0) return 0.0;

        Map<Long, Double> minWeights = eventMinWeightSums.computeIfAbsent(newEventId, v -> new HashMap<>());
        double oldSum = minWeights.getOrDefault(newAnotherEventId, 0.0);

        double oldMin = Math.min(oldWeight, oldAnotherWeight);
        double newMin = Math.min(newWeight, oldAnotherWeight);

        double newSum = oldSum - oldMin + newMin;
        minWeights.put(newAnotherEventId, newSum);

        return newSum;
    }

    private Double calculateSimilarity(long eventId, long anotherEventId, double newMinSum) {
        log.info("Рассчитываем схожесть событий с id = {} и {}", eventId, anotherEventId);
        double sumEvent = eventWeightSums.getOrDefault(eventId, 0.0);
        double sumAnotherEvent = eventWeightSums.getOrDefault(anotherEventId, 0.0);

        return (sumEvent * sumAnotherEvent > 0) ? newMinSum / (Math.sqrt(sumEvent * sumAnotherEvent)) : 0.0;
    }

    private EventSimilarityAvro createAvro(long eventId, long anotherEventId, double similarity, Instant timestamp) {
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