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

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrivateRequestController implements RequestClient {
    final RequestService requestService;

    @Override
    public Collection<RequestDto> getAllUserRequest(Long userId) {
        log.info("Request for all requests of user with id = {}", userId);
        return requestService.getAllUserRequest(userId);
    }

    @Override
    public RequestDto createRequest(Long userId, Long eventId) {
        log.info("Request for create request for user with id = {} and event with id = {}", userId, eventId);
        return requestService.createRequest(userId, eventId);
    }

    @Override
    public RequestDto cancelRequest(Long userId, Long requestId) {
        log.info("Request for cancel request with id = {} for user with id = {}", requestId, userId);
        return requestService.cancelRequest(userId, requestId);
    }
}