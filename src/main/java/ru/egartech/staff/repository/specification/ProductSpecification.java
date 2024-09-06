package ru.egartech.staff.repository.specification;

import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import ru.egartech.staff.entity.ProductEntity;

@UtilityClass
public class ProductSpecification {

    public static Specification<ProductEntity> hasName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            String namePattern = "%" + name.trim().toLowerCase() + "%";
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + namePattern + "%");
        };
    }

    public static Specification<ProductEntity> hasType(String type) {
        return (root, query, criteriaBuilder) -> {
            if (type == null || type.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            String typePattern = type.trim().toUpperCase() + "%";
            return criteriaBuilder.like(root.get("type").as(String.class), typePattern);
        };
    }
}
