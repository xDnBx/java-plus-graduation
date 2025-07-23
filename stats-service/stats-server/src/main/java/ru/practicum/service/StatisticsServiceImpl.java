package ru.practicum.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.stats.GetResponse;
import ru.practicum.dto.stats.HitRequest;
import ru.practicum.mapper.HitMapper;
import ru.practicum.model.Hit;
import ru.practicum.repository.HitRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatisticsServiceImpl implements StatisticsService {
    final HitMapper hitMapper;
    final HitRepository hitRepository;
    final EntityManager entityManager;

    @Override
    @Transactional
    public void createHit(HitRequest hitRequest) {
        hitRepository.save(hitMapper.requestToHit(hitRequest));
        log.info("Hit saved");
    }

    @Override
    public List<GetResponse> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date should not be after end date");
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<Hit> hit = query.from(Hit.class);

        Predicate whereClause = cb.between(hit.get("timestamp"), start.minusNanos(start.getNano()), end);

        if (uris != null && !uris.isEmpty()) {
            whereClause = cb.and(whereClause, hit.get("uri").in(uris));
        }

        if (unique) {
            query.multiselect(
                    cb.countDistinct(hit.get("ip")),
                    hit.get("app"),
                    hit.get("uri")
            );
        } else {
            query.multiselect(
                    cb.count(hit.get("ip")),
                    hit.get("app"),
                    hit.get("uri")
            );
        }

        query.where(whereClause)
                .groupBy(hit.get("app"), hit.get("uri"))
                .orderBy(cb.desc(cb.count(hit.get("ip"))));

        List<Tuple> tuples = entityManager.createQuery(query).getResultList();

        log.info("Returned list of getResponses");
        return tuples.stream().map(tuple ->
                GetResponse.builder()
                        .hits(tuple.get(0, Long.class))
                        .app(tuple.get(1, String.class))
                        .uri(tuple.get(2, String.class))
                        .build()
        ).toList();
    }
}