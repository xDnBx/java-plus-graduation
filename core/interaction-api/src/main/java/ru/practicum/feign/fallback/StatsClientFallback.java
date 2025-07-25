package ru.practicum.feign.fallback;

import org.springframework.stereotype.Component;
import ru.practicum.dto.stats.GetResponse;
import ru.practicum.dto.stats.HitRequest;
import ru.practicum.exception.ServiceUnavailableException;
import ru.practicum.feign.StatsClient;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class StatsClientFallback implements StatsClient {
    @Override
    public void addHit(HitRequest hitRequest) {
        throw new ServiceUnavailableException("Stats-Service is unavailable");
    }

    @Override
    public List<GetResponse> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        throw new ServiceUnavailableException("Stats-Service is unavailable");
    }
}