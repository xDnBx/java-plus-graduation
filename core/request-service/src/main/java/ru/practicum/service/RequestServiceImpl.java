package ru.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.enums.EventState;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.dto.request.enums.RequestStatus;
import ru.practicum.exception.*;
import ru.practicum.feign.EventClient;
import ru.practicum.feign.UserClient;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.Request;
import ru.practicum.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestServiceImpl implements RequestService {
    final RequestRepository requestRepository;
    final RequestMapper requestMapper;
    final UserClient userClient;
    final EventClient eventClient;

    @Override
    public Collection<RequestDto> getAllUserRequest(Long userId) {
        userClient.getUserById(userId);
        log.info("Get requests by userId = {}", userId);
        return requestRepository.findAllByRequesterId(userId).stream().map(requestMapper::toDto).toList();
    }

    @Override
    @Transactional
    public RequestDto createRequest(Long userId, Long eventId) {
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new DuplicateRequestException("Request can be only one");
        }
        EventFullDto event = eventClient.getEventByIdFeign(eventId);
        RequestStatus status = RequestStatus.PENDING;

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotPublishedEventRequestException("Event must be published");
        }

        int requestsSize = requestRepository.findAllByEventId(eventId).size();

        if (event.getParticipantLimit() != 0 && requestsSize >= event.getParticipantLimit()) {
            throw new RequestLimitException("No more seats for the event");
        }

        if (event.getParticipantLimit() == 0) {
            status = RequestStatus.CONFIRMED;
        }

        if (event.getInitiator().getId().equals(userId)) {
            throw new InitiatorRequestException("Initiator can't submit a request for event");
        }

        Request request = Request.builder()
                .created(LocalDateTime.now())
                .requesterId(userId)
                .eventId(event.getId())
                .status(status)
                .build();
        log.info("Post request body = {}", request);
        return requestMapper.toDto(requestRepository.save(request));
    }

    @Override
    public RequestDto cancelRequest(Long userId, Long requestId) {
        userClient.getUserById(userId);
        Request request = requestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException("Request not found"));
        request.setStatus(RequestStatus.CANCELED);
        log.info("Cancel request by requestId = {} and userId = {}",requestId,userId);
        return requestMapper.toDto(requestRepository.save(request));
    }
}