package ru.practicum.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.feign.RequestClient;
import ru.practicum.service.RequestService;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/requests/feign")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestController implements RequestClient {
    final RequestService requestService;

    @Override
    public Map<Long, List<RequestDto>> getConfirmedRequests(List<Long> eventIds) {
        log.info("Request for confirmed requests for events = {}", eventIds);
        return requestService.getConfirmedRequests(eventIds);
    }
}