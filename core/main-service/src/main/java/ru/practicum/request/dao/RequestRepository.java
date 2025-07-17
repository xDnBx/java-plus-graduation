package ru.practicum.request.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.enums.RequestStatus;

import java.util.List;
import java.util.Set;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long>, JpaSpecificationExecutor<Request> {
    boolean existsByRequesterIdAndEventId(Long requesterId, Long eventId);

    Set<Request> findAllByRequesterId(Long requesterId);

    Set<Request> findAllByEventId(Long eventId);

    List<Request> findAllByIdIn(List<Long> requestIds);

    List<Request> findAllByEventIdAndStatus(Long eventId, RequestStatus status);

    List<Request> findAllByEventIdInAndStatus(List<Long> eventIds, RequestStatus status);
}