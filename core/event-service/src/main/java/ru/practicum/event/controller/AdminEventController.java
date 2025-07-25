package ru.practicum.event.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.AdminPatchEventDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.GetAllEventsAdminParams;
import ru.practicum.event.service.EventService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminEventController {
    final EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<EventFullDto> getAllEvents(
            @RequestParam(required = false) Set<Long> users,
            @RequestParam(required = false) Set<String> states,
            @RequestParam(required = false) Set<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0", required = false) Integer from,
            @RequestParam(defaultValue = "10", required = false) Integer size) {
        log.info("Get all events with params");
        return eventService.getAllEventsAdmin(GetAllEventsAdminParams.builder()
                .users(users)
                .states(states)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .from(from)
                .size(size)
                .build());
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto patchEventById(@Valid @RequestBody AdminPatchEventDto adminPatchEventDto,
                                       @PathVariable Long eventId) {
        log.info("Patch event by id {}", eventId);
        return eventService.patchEventById(eventId, adminPatchEventDto);
    }
}