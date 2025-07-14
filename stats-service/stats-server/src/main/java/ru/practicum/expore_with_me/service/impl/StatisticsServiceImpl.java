package ru.practicum.expore_with_me.service.impl;

import dto.GetResponse;
import dto.HitRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.expore_with_me.dao.HitRepository;
import ru.practicum.expore_with_me.mapper.HitMapper;
import ru.practicum.expore_with_me.model.Hit;
import ru.practicum.expore_with_me.service.StatisticsService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    private final HitMapper hitMapper;
    private final HitRepository hitRepository;
    private final EntityManager entityManager;

    @Override
    public void createHit(HitRequest hitRequest) {
        Hit hit = hitMapper.requestToHit(hitRequest);
        hitRepository.save(hit);
        log.info("Hit saved");
    }

    @Override
    public Collection<GetResponse> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
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
