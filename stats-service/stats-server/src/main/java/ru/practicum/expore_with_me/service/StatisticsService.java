package ru.practicum.expore_with_me.service;

import dto.GetResponse;
import dto.HitRequest;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface StatisticsService {
    void createHit(HitRequest hitRequest);

    Collection<GetResponse> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
