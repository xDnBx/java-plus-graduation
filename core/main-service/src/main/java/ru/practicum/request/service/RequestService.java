package ru.practicum.request.service;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.request.dto.RequestDto;

import java.util.Collection;

public interface RequestService {
    Collection<RequestDto> getAllUserRequest(@PathVariable Long userId);

    RequestDto createRequest(@PathVariable Long userId, @RequestParam Long eventId);

    RequestDto cancelRequest(@PathVariable Long userId, @RequestParam Long requestId);
}