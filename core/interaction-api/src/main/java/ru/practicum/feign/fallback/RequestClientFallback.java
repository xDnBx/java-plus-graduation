package ru.practicum.feign.fallback;

import org.springframework.stereotype.Component;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.exception.ServiceUnavailableException;
import ru.practicum.feign.RequestClient;

import java.util.Collection;

@Component
public class RequestClientFallback implements RequestClient {
    @Override
    public Collection<RequestDto> getAllUserRequest(Long userId) {
        throw new ServiceUnavailableException("Request-Service is unavailable");
    }

    @Override
    public RequestDto createRequest(Long userId, Long eventId) {
        throw new ServiceUnavailableException("Request-Service is unavailable");
    }

    @Override
    public RequestDto cancelRequest(Long userId, Long requestId) {
        throw new ServiceUnavailableException("Request-Service is unavailable");
    }
}