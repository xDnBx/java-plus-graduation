package ru.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.UserActionClient;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventRequestStatusUpdateRequest;
import ru.practicum.dto.event.EventRequestStatusUpdateResult;
import ru.practicum.dto.event.enums.EventState;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.dto.request.enums.RequestStatus;
import ru.practicum.exception.*;
import ru.practicum.feign.EventClient;
import ru.practicum.feign.UserClient;
import ru.practicum.grpc.stats.action.ActionTypeProto;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.Request;
import ru.practicum.repository.RequestRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestServiceImpl implements RequestService {
    final RequestRepository requestRepository;
    final RequestMapper requestMapper;
    final UserClient userClient;
    final EventClient eventClient;
    final UserActionClient userActionClient;

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

        userActionClient.collectUserAction(userId, eventId, ActionTypeProto.ACTION_REGISTER, Instant.now());

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

    @Override
    public Map<Long, List<RequestDto>> getConfirmedRequests(List<Long> eventIds) {
        List<Request> confirmedRequestsByEventId = requestRepository.findAllByEventIdInAndStatus(eventIds,
                RequestStatus.CONFIRMED);
        return confirmedRequestsByEventId.stream()
                .map(requestMapper::toDto)
                .collect(Collectors.groupingBy(RequestDto::getEvent));
    }

    @Override
    public Collection<RequestDto> getRequests(Long userId, Long eventId) {
        return requestRepository.findAllByEventId(eventId).stream().map(requestMapper::toDto).toList();
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequest(Long userId, Long eventId,
                                                        EventRequestStatusUpdateRequest updateRequest) {
        EventFullDto event = eventClient.getEventByUserFeign(userId, eventId);

        List<Request> requests = requestRepository.findAllByIdIn(updateRequest.getRequestIds());

        for (Request request : requests) {
            if (!request.getEventId().equals(eventId)) {
                throw new NotFoundException("Request with requestId = " + request.getId() + "does not match eventId = " + eventId);
            }
        }

        int confirmedCount = requestRepository.findAllByEventIdAndStatus(eventId, RequestStatus.CONFIRMED).size();
        int size = updateRequest.getRequestIds().size();
        int confirmedSize = updateRequest.getStatus().equals(RequestStatus.CONFIRMED) ? size : 0;

        if (event.getParticipantLimit() != 0 && confirmedCount + confirmedSize > event.getParticipantLimit()) {
            throw new TooManyRequestsException("Event limit exceed");
        }

        List<RequestDto> confirmedRequests = new ArrayList<>();
        List<RequestDto> rejectedRequests = new ArrayList<>();

        for (Request request : requests) {
            if (updateRequest.getStatus().equals(RequestStatus.CONFIRMED)) {
                request.setStatus(RequestStatus.CONFIRMED);
                confirmedRequests.add(requestMapper.toDto(request));
            } else if (updateRequest.getStatus().equals(RequestStatus.REJECTED)) {
                if (request.getStatus().equals(RequestStatus.CONFIRMED)) {
                    throw new AlreadyConfirmedException("The request cannot be rejected if it is confirmed");
                }
                request.setStatus(RequestStatus.REJECTED);
                rejectedRequests.add(requestMapper.toDto(request));
            }
        }

        requestRepository.saveAll(requests);

        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmedRequests)
                .rejectedRequests(rejectedRequests)
                .build();
    }

    @Override
    public boolean isRequestExists(Long requesterId, Long eventId) {
        return requestRepository.existsByRequesterIdAndEventId(requesterId, eventId);
    }
}