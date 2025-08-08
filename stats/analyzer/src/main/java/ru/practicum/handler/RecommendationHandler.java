package ru.practicum.handler;

import ru.practicum.grpc.stats.recommendation.InteractionsCountRequestProto;
import ru.practicum.grpc.stats.recommendation.RecommendedEventProto;
import ru.practicum.grpc.stats.recommendation.SimilarEventsRequestProto;
import ru.practicum.grpc.stats.recommendation.UserPredictionsRequestProto;

import java.util.List;

public class RecommendationHandler {
    public List<RecommendedEventProto> getRecommendationsForUser(UserPredictionsRequestProto request) {
        return null;
    }

    public List<RecommendedEventProto> getSimilarEvents(SimilarEventsRequestProto request) {
        return null;
    }

    public List<RecommendedEventProto> getInteractionsCount(InteractionsCountRequestProto request) {
        return null;
    }
}