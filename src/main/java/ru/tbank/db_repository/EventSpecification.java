package ru.tbank.db_repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import ru.tbank.entities.Event;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;

public class EventSpecification {

    public static Specification<Event> findByName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(name)) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(root.get("name"), "%" + name + "%");
        };
    }

    public static Specification<Event> findByLocation(Long locationId) {
        return (root, query, criteriaBuilder) -> {
            if (locationId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("location").get("locationId"), locationId);
        };
    }

    public static Specification<Event> findByDateRange(LocalDateTime fromDate, LocalDateTime toDate) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            if (fromDate != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"), fromDate));
            }
            if (toDate != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(root.get("endDate"), toDate));
            }
            return predicate;
        };
    }
}
