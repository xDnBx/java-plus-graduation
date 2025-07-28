package ru.practicum.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.service.RequestService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrivateRequestController {
    final RequestService requestService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<RequestDto> getAllUserRequest(@PathVariable Long userId) {
        log.info("Request for all requests of user with id = {}", userId);
        return requestService.getAllUserRequest(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto createRequest(@PathVariable Long userId, @RequestParam Long eventId) {
        log.info("Request for create request for user with id = {} and event with id = {}", userId, eventId);
        return requestService.createRequest(userId, eventId);
    }

    @PatchMapping("{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public RequestDto cancelRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        log.info("Request for cancel request with id = {} for user with id = {}", requestId, userId);
        return requestService.cancelRequest(userId, requestId);
    }
}