package ru.practicum.feign;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.feign.fallback.EventClientFallback;

@FeignClient(name = "event-service", path = "/events/feign", fallback = EventClientFallback.class)
public interface EventClient {
    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    EventFullDto getEventByIdFeign(@PathVariable Long eventId) throws FeignException;
}