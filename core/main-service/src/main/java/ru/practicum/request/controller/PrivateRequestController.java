package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.service.RequestService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class PrivateRequestController {
    final RequestService requestService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<RequestDto> getAllUserRequest(@PathVariable Long userId) {
        return requestService.getAllUserRequest(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto createRequest(@PathVariable Long userId, @RequestParam Long eventId) {
        return requestService.createRequest(userId, eventId);
    }

    @PatchMapping("{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public RequestDto cancelRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        return requestService.cancelRequest(userId, requestId);
    }
}