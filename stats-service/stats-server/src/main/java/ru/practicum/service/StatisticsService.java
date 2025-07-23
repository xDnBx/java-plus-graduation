package ru.practicum.service;

import ru.practicum.dto.stats.GetResponse;
import ru.practicum.dto.stats.HitRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticsService {
    void createHit(HitRequest hitRequest);

    List<GetResponse> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}