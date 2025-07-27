package ru.practicum.feign.fallback;

import org.springframework.stereotype.Component;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.exception.ServiceUnavailableException;
import ru.practicum.feign.RequestClient;

import java.util.List;
import java.util.Map;

@Component
public class RequestClientFallback implements RequestClient {
    @Override
    public Map<Long, List<RequestDto>> getConfirmedRequests(List<Long> eventIds) {
        throw new ServiceUnavailableException("Request-Service is unavailable");
    }
}