package ru.practicum.feign.fallback;

import org.springframework.stereotype.Component;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.exception.ServiceUnavailableException;
import ru.practicum.feign.EventClient;

@Component
public class EventClientFallback implements EventClient {
    @Override
    public EventFullDto getEventByIdFeign(Long eventId) {
        throw new ServiceUnavailableException("Event-Service is unavailable");
    }
}