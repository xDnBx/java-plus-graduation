package ru.practicum.event.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.validation.ValidationException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.RecommendationsClient;
import ru.practicum.UserActionClient;
import ru.practicum.dto.event.*;
import ru.practicum.dto.event.enums.EventState;
import ru.practicum.dto.event.enums.EventStateAction;
import ru.practicum.dto.event.enums.SortType;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.Location;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.repository.LocationRepository;
import ru.practicum.event.specification.EventFindSpecification;
import ru.practicum.exception.*;
import ru.practicum.feign.RequestClient;
import ru.practicum.feign.UserClient;
import ru.practicum.grpc.stats.action.ActionTypeProto;
import ru.practicum.grpc.stats.recommendation.RecommendedEventProto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventServiceImpl implements EventService {
    final EventRepository eventRepository;
    final RequestClient requestClient;
    final EventMapper eventMapper;
    final LocationRepository locationRepository;
    final UserClient userClient;
    final EntityManager entityManager;
    final RecommendationsClient recommendationsClient;
    final UserActionClient userActionClient;

    @Override
    public Collection<EventShortDto> getAllEvents(Long userId, Integer from, Integer size) {
        int pageNumber = from / size;
        Pageable pageable = PageRequest.of(pageNumber, size);

        Page<Event> page = eventRepository.findAllByInitiatorId(userId, pageable);

        log.info("Get events with {userId, from, size} = ({}, {}, {})", userId, from, size);
        return page.getContent().stream().map(eventMapper::toShortDto).toList();
    }

    @Override
    public Collection<EventFullDto> getAllEventsAdmin(GetAllEventsAdminParams params) {
        Set<Long> users = params.getUsers();
        Set<String> states = params.getStates();
        Set<Long> categories = params.getCategories();
        LocalDateTime rangeStart = params.getRangeStart();
        LocalDateTime rangeEnd = params.getRangeEnd();
        Integer from = params.getFrom();
        Integer size = params.getSize();

        int pageNumber = from / size;
        Pageable pageable = PageRequest.of(pageNumber, size);

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(Event.class);
        Root<Event> root = criteriaQuery.from(Event.class);
        criteriaQuery.select(root);

        Specification<Event> specification = Specification
                .where(EventFindSpecification.userIn(users))
                .and(EventFindSpecification.stateIn(states))
                .and(EventFindSpecification.categoryIn(categories))
                .and(EventFindSpecification.eventDateAfter(rangeStart))
                .and(EventFindSpecification.eventDateBefore(rangeEnd));
        Page<Event> page = eventRepository.findAll(specification, pageable);

        log.info("Get events with {users, states, categories, rangeStart, rangeEnd, from, size} = ({},{},{},{},{},{},{})",
                users, size, categories, rangeStart, rangeEnd, from, size);

        Map<Long, List<RequestDto>> eventIdToConfirmedRequests = requestClient.getConfirmedRequests(page.stream()
                .map(Event::getId).toList());
        List<Long> initiatorIds = page.stream().map(Event::getInitiatorId).toList();
        Map<Long, UserDto> usersMap = userClient.getUsers(initiatorIds, 0, initiatorIds.size()).stream()
                .collect(Collectors.toMap(UserDto::getId, Function.identity()));

        return page.stream()
                .map(event -> {
                    EventFullDto eventFullDto = eventMapper.toFullDto(event);
                    eventFullDto.setConfirmedRequests((long) eventIdToConfirmedRequests.getOrDefault(
                            event.getId(),
                            Collections.emptyList()).size());
                    long initiatorId = usersMap.get(event.getInitiatorId()).getId();
                    String initiatorName = usersMap.get(event.getInitiatorId()).getName();
                    eventFullDto.setInitiator(UserShortDto.builder().id(initiatorId).name(initiatorName).build());
                    return eventFullDto;
                })
                .toList();
    }

    @Override
    public Collection<EventShortDto> getAllEventsPublic(GetAllEventsPublicParams params) {
        String text = params.getText();
        Set<Long> categories = params.getCategories();
        Boolean paid = params.getPaid();
        LocalDateTime rangeStart = params.getRangeStart();
        LocalDateTime rangeEnd = params.getRangeEnd();
        Boolean onlyAvailable = params.getOnlyAvailable();
        SortType sort = params.getSort();
        Integer from = params.getFrom();
        Integer size = params.getSize();

        Pageable pageable = PageRequest.of(from / size, size);

        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }

        if (rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException("Time period incorrect");
        }

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(Event.class);
        Root<Event> root = criteriaQuery.from(Event.class);
        criteriaQuery.select(root);

        Specification<Event> specification = Specification
                .where(EventFindSpecification.textInAnnotationOrDescription(text))
                .and(EventFindSpecification.categoryIn(categories))
                .and(EventFindSpecification.eventDateAfter(rangeStart))
                .and(EventFindSpecification.eventDateBefore(rangeEnd))
                .and(EventFindSpecification.isAvailable(onlyAvailable))
                .and(EventFindSpecification.sortBySortType(sort))
                .and(EventFindSpecification.onlyPublished());
        Page<Event> page = eventRepository.findAll(specification, pageable);

        log.info("Get events with {text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size} = ({},{},{},{},{},{},{},{},{})",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        return page.stream()
                .map(eventMapper::toShortDto)
                .toList();
    }

    @Override
    @Transactional
    public EventFullDto patchEventById(Long eventId, AdminPatchEventDto adminPatchEventDto) {
        Event event = findEventById(eventId);

        LocalDateTime updateStartDate = adminPatchEventDto.getEventDate();

        if (event.getState().equals(EventState.PUBLISHED) && LocalDateTime.now().isAfter(event.getPublishedOn().plusHours(1))) {
            throw new PublicationException("Change event no later than one hour before the start");
        }

        if (updateStartDate != null && updateStartDate.isBefore(LocalDateTime.now())) {
            throw new UpdateStartDateException("Date and time has already arrived");
        }

        EventStateAction updateStateAction = getUpdateStateAction(adminPatchEventDto, event);

        stateChanger(event, updateStateAction);

        if (adminPatchEventDto.getLocation() != null) {
            Location location = findLocationByLatAndLon(adminPatchEventDto.getLocation().getLat(),
                    adminPatchEventDto.getLocation().getLon());
            event.setLocation(location);
        }

        eventMapper.patchUserRequest(adminPatchEventDto, event);
        if (event.getState().equals(EventState.PUBLISHED)) {
            event.setPublishedOn(LocalDateTime.now());
        }

        log.info("Patch event with eventId = {}", eventId);
        return eventMapper.toFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        Event event = eventMapper.toEvent(newEventDto);
        Location location = findLocationByLatAndLon(newEventDto.getLocation().getLat(),
                newEventDto.getLocation().getLon());

        findUserById(userId);

        event.setInitiatorId(userId);
        event.setLocation(location);

        log.info("Create new event with userId = {}", userId);
        return eventMapper.toFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto getEventById(Long userId, Long eventId) {
        Event event = findEventByIdAndInitiatorId(userId, eventId);
        return eventMapper.toFullDto(event);
    }

    @Override
    public EventFullDto getEventByIdPublic(Long userId, Long eventId) {
        Event event = findEventById(eventId);

        if (event.getState() != EventState.PUBLISHED) {
            throw new GetPublicEventException("Event must be published");
        }

        userActionClient.collectUserAction(userId, eventId, ActionTypeProto.ACTION_VIEW, Instant.now());

        EventFullDto eventFullDto = eventMapper.toFullDto(event);

        List<RequestDto> confirmedRequests = requestClient.getConfirmedRequests(List.of(event.getId()))
                .get(event.getId());
        eventFullDto.setConfirmedRequests((long) (confirmedRequests == null ? 0 : confirmedRequests.size()));

        return eventFullDto;
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateRequest) {
        Event event = findEventByIdAndInitiatorId(userId, eventId);

        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new AlreadyPublishedException("Event with eventId = " + eventId + "has already been published");
        }

        stateChanger(event, updateRequest.getStateAction());
        if (updateRequest.getLocation() != null) {
            Location location = findLocationByLatAndLon(updateRequest.getLocation().getLat(),
                    updateRequest.getLocation().getLon());
            event.setLocation(location);
        }
        eventMapper.updateUserRequest(updateRequest, event);
        log.info("Update event with eventId = {}", eventId);
        return eventMapper.toFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto getEventByIdFeign(Long eventId) {
        Event event = findEventById(eventId);
        EventFullDto eventFullDto = eventMapper.toFullDto(event);
        UserDto userDto = userClient.getUserById(event.getInitiatorId());
        UserShortDto userShortDto = UserShortDto.builder().id(userDto.getId()).name(userDto.getName()).build();
        eventFullDto.setInitiator(userShortDto);
        return eventFullDto;
    }

    @Override
    public EventFullDto getEventByUserFeign(Long userId, Long eventId) {
        return getEventById(userId, eventId);
    }

    @Override
    public List<EventFullDto> getRecommendations(Long userId) {
        Set<Long> eventIds = recommendationsClient.getRecommendationsForUser(userId, 10)
                .map(RecommendedEventProto::getEventId).collect(Collectors.toSet());

        return eventRepository
                .findAllByIdIn(eventIds)
                .stream()
                .map(eventMapper::toFullDto)
                .toList();
    }

    @Override
    public void likeEvent(Long userId, Long eventId) {
        if (!requestClient.isRequestExists(userId, eventId)) {
            throw new ValidationException("User has not confirmed the request");
        }

        userActionClient.collectUserAction(userId, eventId, ActionTypeProto.ACTION_LIKE, Instant.now());
    }

    private void stateChanger(Event event, EventStateAction stateAction) {
        if (stateAction != null) {
            Map<EventStateAction, EventState> state = Map.of(
                    EventStateAction.SEND_TO_REVIEW, EventState.PENDING,
                    EventStateAction.CANCEL_REVIEW, EventState.CANCELED,
                    EventStateAction.PUBLISH_EVENT, EventState.PUBLISHED,
                    EventStateAction.REJECT_EVENT, EventState.CANCELED);
            event.setState(state.get(stateAction));
        }
    }

    private EventStateAction getUpdateStateAction(AdminPatchEventDto adminPatchEventDto, Event event) {
        EventStateAction updateStateAction = adminPatchEventDto.getStateAction();

        if (updateStateAction != null && !event.getState().equals(EventState.PENDING) && updateStateAction.equals(EventStateAction.PUBLISH_EVENT)) {
            throw new PublicationException("The event can only be published during the pending stage");
        }

        if (updateStateAction != null && updateStateAction.equals(EventStateAction.REJECT_EVENT)
                && event.getState().equals(EventState.PUBLISHED)) {
            throw new PublicationException("Cannot reject a published event");
        }
        return updateStateAction;
    }

    private Event findEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id = %s, not found", eventId)));
    }

    private Event findEventByIdAndInitiatorId(Long userId, Long eventId) {
        return eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id = %d and userId=%d not found",
                        eventId, userId)));
    }

    private void findUserById(Long userId) {
        userClient.getUserById(userId);
    }

    private Location findLocationByLatAndLon(Float lat, Float lon) {
        return locationRepository
                .findByLatAndLon(lat, lon)
                .orElseGet(() -> locationRepository.save(Location.builder().lat(lat).lon(lon).build()));
    }
}