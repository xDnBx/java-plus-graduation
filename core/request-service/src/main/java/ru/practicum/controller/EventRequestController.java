package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventRequestStatusUpdateRequest;
import ru.practicum.dto.event.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.service.RequestService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events/{eventId}/requests")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRequestController {
    final RequestService requestService;

    @GetMapping
    public Collection<RequestDto> getRequests(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Get requests for event with id = {} for user with id = {}", eventId, userId);
        return requestService.getRequests(userId, eventId);
    }

    @PatchMapping
    public EventRequestStatusUpdateResult updateRequest(@PathVariable Long userId,
                                                        @PathVariable Long eventId,
                                                        @Valid @RequestBody EventRequestStatusUpdateRequest updateRequest) {
        log.info("Update request for event with id = {} for user with id = {}", eventId, userId);
        return requestService.updateRequest(userId, eventId, updateRequest);
    }
}