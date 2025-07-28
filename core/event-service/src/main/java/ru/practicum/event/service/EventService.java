package ru.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.dto.event.*;

import java.util.Collection;

public interface EventService {
    Collection<EventShortDto> getAllEvents(Long userId, Integer from, Integer size);

    Collection<EventFullDto> getAllEventsAdmin(GetAllEventsAdminParams params);

    Collection<EventShortDto> getAllEventsPublic(GetAllEventsPublicParams params);

    EventFullDto patchEventById(Long eventId, AdminPatchEventDto adminPatchEventDto);

    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getEventById(Long userId, Long eventId);

    EventFullDto getEventByIdFeign(Long eventId);

    EventFullDto getEventByIdPublic(Long eventId, HttpServletRequest httpServletRequest);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateRequest);

    EventFullDto getEventByUserFeign(Long eventId, Long userId);
}