package ru.practicum.explore_with_me.compilations.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.practicum.explore_with_me.compilations.model.Compilation;

public class CompilationFindSpecification {
    public static Specification<Compilation> byPinned(Boolean pinned) {
        if (pinned == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("pinned"), pinned);
    }
}
