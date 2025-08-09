package ru.practicum.handler;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import ru.practicum.grpc.stats.recommendation.InteractionsCountRequestProto;
import ru.practicum.grpc.stats.recommendation.RecommendedEventProto;
import ru.practicum.grpc.stats.recommendation.SimilarEventsRequestProto;
import ru.practicum.grpc.stats.recommendation.UserPredictionsRequestProto;
import ru.practicum.model.ActionType;
import ru.practicum.model.EventSimilarity;
import ru.practicum.model.UserAction;
import ru.practicum.repository.SimilarityRepository;
import ru.practicum.repository.UserActionRepository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecommendationsHandler {
    final UserActionRepository userActionRepository;
    final SimilarityRepository similarityRepository;

    @Value("${user-action.view:0.4}")
    Double viewAction;

    @Value("${user-action.register:0.8}")
    Double registerAction;

    @Value("${user-action.like:1.0}")
    Double likeAction;

    public List<RecommendedEventProto> getRecommendationsForUser(UserPredictionsRequestProto request) {
        Long userId = request.getUserId();
        int limit = request.getMaxResults();
        PageRequest pageRequest = PageRequest.of(0, limit,
                Sort.by(Sort.Direction.DESC, "timestamp"));

        // Получаем последние просмотренные события пользователя
        Set<Long> recentlyViewedEventIds = userActionRepository.findAllByUserId(userId, pageRequest).stream()
                .map(UserAction::getEventId)
                .collect(Collectors.toSet());

        if (recentlyViewedEventIds.isEmpty()) {
            return Collections.emptyList();
        }

        // Находим похожие события для рекомендаций
        Set<Long> candidateEventIds = findCandidateRecommendations(userId, recentlyViewedEventIds, limit);

        // Рассчитываем score для каждого кандидата и формируем рекомендации
        return generateRecommendations(candidateEventIds, userId, limit);
    }

    public List<RecommendedEventProto> getSimilarEvents(SimilarEventsRequestProto request) {
        Long eventId = request.getEventId();
        Long userId = request.getUserId();
        PageRequest pageRequest = PageRequest.of(0, request.getMaxResults(),
                Sort.by(Sort.Direction.DESC, "score"));

        List<EventSimilarity> similaritiesA = similarityRepository.findAllByEventA(eventId, pageRequest);
        List<EventSimilarity> similaritiesB = similarityRepository.findAllByEventB(eventId, pageRequest);

        List<RecommendedEventProto> recommendations = new ArrayList<>();

        addFilteredRecommendations(recommendations, similaritiesA,true, userId);
        addFilteredRecommendations(recommendations, similaritiesB,false, userId);

        recommendations.sort(Comparator.comparing(RecommendedEventProto::getScore).reversed());

        return recommendations.size() > request.getMaxResults()
                ? recommendations.subList(0, request.getMaxResults())
                : recommendations;
    }

    public List<RecommendedEventProto> getInteractionsCount(InteractionsCountRequestProto request) {
        Set<Long> eventIds = new HashSet<>(request.getEventIdList());

        Map<Long, Double> eventScores = new HashMap<>();

        userActionRepository.findAllByEventIdIn(eventIds).forEach(action -> {
            long eventId = action.getEventId();
            double weight = toWeight(action.getActionType());
            eventScores.merge(eventId, weight, Double::sum);
        });

        return eventScores.entrySet().stream()
                .map(entry -> RecommendedEventProto.newBuilder()
                        .setEventId(entry.getKey())
                        .setScore(entry.getValue())
                        .build())
                .toList();
    }

    private Set<Long> findCandidateRecommendations(Long userId, Set<Long> viewedEventIds, int limit) {
        // Получаем похожие события в обоих направлениях (A→B и B→A)
        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "score"));

        List<EventSimilarity> similaritiesA = similarityRepository.findAllByEventAIn(viewedEventIds, pageRequest);
        List<EventSimilarity> similaritiesB = similarityRepository.findAllByEventBIn(viewedEventIds, pageRequest);

        // Собираем уникальные ID событий, которые пользователь еще не видел
        Set<Long> recommendations = new HashSet<>();

        addNewEventsFromSimilarities(similaritiesA, true, userId, recommendations);
        addNewEventsFromSimilarities(similaritiesB, false, userId, recommendations);

        return recommendations;
    }

    private void addNewEventsFromSimilarities(List<EventSimilarity> similarities,
                                              boolean isEventB,
                                              Long userId,
                                              Set<Long> result) {
        for (EventSimilarity es : similarities) {
            Long candidateId = isEventB ? es.getEventB() : es.getEventA();
            if (!userActionRepository.existsByEventIdAndUserId(candidateId, userId)) {
                result.add(candidateId);
            }
        }
    }

    private List<RecommendedEventProto> generateRecommendations(Set<Long> candidateEventIds,
                                                                Long userId,
                                                                int limit) {
        // Рассчитываем score для каждого кандидата
        Map<Long, Double> eventScores = candidateEventIds.stream()
                .collect(Collectors.toMap(eventId -> eventId,
                        eventId -> calculateRecommendationScore(eventId, userId, limit)));

        // Сортируем по score и ограничиваем количество
        return eventScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> buildRecommendation(entry.getKey(), entry.getValue()))
                .toList();
    }

    private Double calculateRecommendationScore(Long eventId, Long userId, int limit) {
        // Получаем похожие события для текущего eventId
        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "score"));
        List<EventSimilarity> similaritiesA = similarityRepository.findAllByEventA(eventId, pageRequest);
        List<EventSimilarity> similaritiesB = similarityRepository.findAllByEventB(eventId, pageRequest);

        // Собираем оценки похожих событий, которые пользователь уже видел
        Map<Long, Double> similarityScores = new HashMap<>();
        collectViewedSimilarities(similaritiesA, true, userId, similarityScores);
        collectViewedSimilarities(similaritiesB, false, userId, similarityScores);

        // Получаем оценки пользователя для этих событий
        Map<Long, Double> userRatings = userActionRepository.findAllByEventIdInAndUserId(
                        similarityScores.keySet(), userId).stream()
                .collect(Collectors.toMap(UserAction::getEventId,
                        userAction -> toWeight(userAction.getActionType())));

        // Рассчитываем взвешенную оценку
        double sumWeightedRatings = 0.0;
        double sumSimilarityScores = 0.0;

        for (Map.Entry<Long, Double> entry : similarityScores.entrySet()) {
            Long viewedEventId = entry.getKey();
            Double userRating = userRatings.get(viewedEventId);
            if (userRating != null) {
                sumWeightedRatings += userRating * entry.getValue();
                sumSimilarityScores += entry.getValue();
            }
        }

        return sumSimilarityScores > 0 ? sumWeightedRatings / sumSimilarityScores : 0.0;
    }

    private void collectViewedSimilarities(List<EventSimilarity> similarities,
                                           boolean isEventB,
                                           Long userId,
                                           Map<Long, Double> result) {
        for (EventSimilarity es : similarities) {
            Long relatedEventId = isEventB ? es.getEventB() : es.getEventA();
            if (userActionRepository.existsByEventIdAndUserId(relatedEventId, userId)) {
                result.put(relatedEventId, es.getScore());
            }
        }
    }

    private RecommendedEventProto buildRecommendation(Long eventId, Double score) {
        return RecommendedEventProto.newBuilder()
                .setEventId(eventId)
                .setScore(score)
                .build();
    }

    private void addFilteredRecommendations(List<RecommendedEventProto> recommendations,
                                            List<EventSimilarity> similarities,
                                            boolean isEventB,
                                            Long userId) {
        for (EventSimilarity es : similarities) {
            Long candidateEventId = isEventB ? es.getEventB() : es.getEventA();

            if (!userActionRepository.existsByEventIdAndUserId(candidateEventId, userId)) {
                recommendations.add(RecommendedEventProto.newBuilder()
                        .setEventId(candidateEventId)
                        .setScore(es.getScore())
                        .build());
            }
        }
    }

    private Double toWeight(ActionType actionType) {
        return switch (actionType) {
            case VIEW -> viewAction;
            case REGISTER -> registerAction;
            case LIKE -> likeAction;
        };
    }
}