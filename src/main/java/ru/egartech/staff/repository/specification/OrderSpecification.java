package ru.egartech.staff.repository.specification;

import jakarta.persistence.criteria.Join;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import ru.egartech.staff.entity.OrderDetailsEntity;
import ru.egartech.staff.entity.OrderEntity;

@UtilityClass
public class OrderSpecification {

    public static Specification<OrderEntity> hasStatus(String status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null || status.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            Join<OrderEntity, OrderDetailsEntity> orderDetails = root.join("orderDetails");
            String statusPattern = status.trim().toUpperCase() + "%";
            return criteriaBuilder.like(orderDetails.get("status").as(String.class), statusPattern);
        };
    }
}
