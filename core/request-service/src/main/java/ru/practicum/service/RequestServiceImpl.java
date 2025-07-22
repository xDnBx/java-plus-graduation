package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.dao.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.enums.EventState;
import ru.practicum.dao.RequestRepository;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.Request;
import ru.practicum.request.model.enums.RequestStatus;
import ru.practicum.user.dao.UserRepository;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class RequestServiceImpl implements RequestService {
    final RequestRepository requestRepository;
    final UserRepository userRepository;
    final EventRepository eventRepository;
    final RequestMapper requestMapper;

    @Override
    public Collection<RequestDto> getAllUserRequest(Long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id=%d + not found", userId)));
        Set<Request> requests = requestRepository.findAllByRequesterId(userId);
        log.info("GET requests by userId = {}",userId);
        return requests.stream().map(requestMapper::toRequestDto).toList();
    }

    @Override
    @Transactional
    public RequestDto createRequest(Long userId, Long eventId) {
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new DuplicateRequestException("Request can be only one");
        }
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id = %s, not found", eventId)));
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id=%d + not found", userId)));
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

        if (event.getInitiator().getId().equals(user.getId())) {
            throw new InitiatorRequestException("Initiator can't submit a request for event");
        }

        Request request = Request.builder()
                .created(LocalDateTime.now())
                .requester(user)
                .event(event)
                .status(status)
                .build();
        log.info("POST request body = {}",request);
        return requestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    public RequestDto cancelRequest(Long userId, Long requestId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id=%d + not found", userId)));
        Request request = requestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException("Request not found"));
        request.setStatus(RequestStatus.CANCELED);
        log.info("Cancel request by requestId = {} and userId = {}",requestId,userId);
        return requestMapper.toRequestDto(requestRepository.save(request));
    }
}