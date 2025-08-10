package ru.practicum.feign;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.feign.fallback.RequestClientFallback;

import java.util.List;
import java.util.Map;

@FeignClient(name = "request-service", path = "/requests/feign", fallback = RequestClientFallback.class)
public interface RequestClient {
    @GetMapping("/confirmed")
    Map<Long, List<RequestDto>> getConfirmedRequests(@RequestParam List<Long> eventIds) throws FeignException;

    @GetMapping("/{eventId}/{requesterId}/exists")
    boolean isRequestExists(@PathVariable Long requesterId, @PathVariable Long eventId) throws FeignException;
}