package ru.practicum.expore_with_me.controller;

import dto.GetResponse;
import dto.HitRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.expore_with_me.service.StatisticsService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
public class StatisticsController {
    private final StatisticsService statisticsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    void addHit(@Valid @RequestBody HitRequest hitRequest) {
        log.info("Post request to /hit");
        statisticsService.createHit(hitRequest);
    }

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    Collection<GetResponse> getStatistics(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                          @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                          @RequestParam(required = false) List<String> uris,
                                          @RequestParam(defaultValue = "false") boolean unique) {
        log.info("Get request to /stats");
        return statisticsService.getStatistics(start, end, uris, unique);
    }
}
