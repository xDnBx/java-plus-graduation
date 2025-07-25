package ru.practicum.event.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.event.service.EventService;
import ru.practicum.feign.EventClient;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/events/feign")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventController implements EventClient {
    final EventService eventService;

    @Override
    public EventFullDto getEventByIdFeign(Long eventId) {
        log.info("Request for event with id = {}", eventId);
        return eventService.getEventByIdFeign(eventId);
    }
}