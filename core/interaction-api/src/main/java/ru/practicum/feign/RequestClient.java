package ru.practicum.feign;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.feign.fallback.RequestClientFallback;

import java.util.Collection;

@FeignClient(name = "request-service", path = "/users/{userId}/requests", fallback = RequestClientFallback.class)
public interface RequestClient {
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    Collection<RequestDto> getAllUserRequest(@PathVariable Long userId) throws FeignException;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    RequestDto createRequest(@PathVariable Long userId, @RequestParam Long eventId) throws FeignException;

    @PatchMapping("{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    RequestDto cancelRequest(@PathVariable Long userId, @PathVariable Long requestId) throws FeignException;
}