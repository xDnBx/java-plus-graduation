package ru.practicum.explore_with_me.event.specification;

import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import ru.practicum.explore_with_me.event.model.Event;
import ru.practicum.explore_with_me.event.model.enums.EventState;
import ru.practicum.explore_with_me.event.model.enums.SortType;

import java.time.LocalDateTime;
import java.util.Set;

public class EventFindSpecification {

    public static Specification<Event> categoryIn(Set<Long> categories) {
        return ((root, query, criteriaBuilder) -> {
            if (categories == null || categories.isEmpty()) {
                return null;
            }
            return root.get("category").get("id").in(categories);
        });
    }

    public static Specification<Event> userIn(Set<Long> users) {
        return ((root, query, criteriaBuilder) -> {
            if (users == null || users.isEmpty()) {
                return null;
            }
            return root.get("initiator").get("id").in(users);
        });
    }

    public static Specification<Event> stateIn(Set<String> states) {
        return ((root, query, criteriaBuilder) -> {
            if (states == null || states.isEmpty()) {
                return null;
            }
            return root.get("state").in(states);
        });
    }

    public static Specification<Event> eventDateBefore(LocalDateTime date) {
        return (root, query, criteriaBuilder) -> {
            if (date == null) {
                return null;
            }
            return criteriaBuilder.lessThan(root.get("eventDate"), date);
        };
    }

    public static Specification<Event> eventDateAfter(LocalDateTime date) {
        return (root, query, criteriaBuilder) -> {
            if (date == null) {
                return null;
            }
            return criteriaBuilder.greaterThan(root.get("eventDate"), date);
        };
    }

    public static Specification<Event> textInAnnotationOrDescription(String text) {
        return (root, query, criteriaBuilder) -> {
            if (text == null || text.isEmpty()) {
                return null;
            }

            String lowerCaseText = text.toLowerCase();

            Predicate inAnnotation = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("annotation")),
                    "%" + lowerCaseText + "%"
            );

            Predicate inDescription = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("description")),
                    "%" + lowerCaseText + "%"
            );

            return criteriaBuilder.or(inAnnotation, inDescription);
        };
    }

    public static Specification<Event> isAvailable(boolean onlyAvailable) {
        if (onlyAvailable) {
            return ((root, query, criteriaBuilder) -> {
                Predicate participantLimitNotZero = criteriaBuilder.lessThan(root.get("participantLimit"), 0);
                Predicate availableCondition = criteriaBuilder
                        .lessThan(root.get("confirmedRequests"), root.get("participantLimit"));
                return criteriaBuilder.and(participantLimitNotZero, availableCondition);
            });
        }
        return null;
    }

    public static Specification<Event> sortBySortType(SortType sortType) {
        return (root, query, criteriaBuilder) -> {
            if (sortType != null) {
                Path<?> sortField = null;
                switch (sortType) {
                    case VIEWS -> sortField = root.get("views");
                    case EVENT_DATE -> sortField = root.get("eventDate");
                }
                Order order = criteriaBuilder.desc(sortField);
                query.orderBy(order);
            }
            return null;
        };
    }

    public static Specification<Event> onlyPublished() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("state"), EventState.PUBLISHED);
    }
}
