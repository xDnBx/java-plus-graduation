package ru.practicum.service;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.dto.event.EventRequestStatusUpdateRequest;
import ru.practicum.dto.event.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.RequestDto;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface RequestService {
    Collection<RequestDto> getAllUserRequest(@PathVariable Long userId);

    RequestDto createRequest(@PathVariable Long userId, @RequestParam Long eventId);

    RequestDto cancelRequest(@PathVariable Long userId, @RequestParam Long requestId);

    Map<Long, List<RequestDto>> getConfirmedRequests(List<Long> eventIds);

    Collection<RequestDto> getRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequest(Long userId, Long eventId,
                                                 EventRequestStatusUpdateRequest updateRequest);

    boolean isRequestExists(Long requesterId, Long eventId);
}