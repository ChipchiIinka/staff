package ru.egartech.staff.repository.specification;

import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import ru.egartech.staff.entity.StaffEntity;

@UtilityClass
public class StaffSpecification {

    public static Specification<StaffEntity> hasFullName(String fullName) {
        return (root, query, criteriaBuilder) -> {
            if (fullName == null || fullName.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            String fullNamePattern = "%" + fullName.trim().toLowerCase() + "%";
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("fullName")), fullNamePattern);
        };
    }

    public static Specification<StaffEntity> hasLogin(String login) {
        return (root, query, criteriaBuilder) -> {
            if (login == null || login.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            String loginPattern = "%" + login.trim().toLowerCase() + "%";
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("login")), loginPattern);
        };
    }

    public static Specification<StaffEntity> hasPosition(String position) {
        return (root, query, criteriaBuilder) -> {
            if (position == null || position.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            String positionPattern = position.trim().toUpperCase() + "%";
            return criteriaBuilder.like(root.get("position").as(String.class), positionPattern);
        };
    }

    public static Specification<StaffEntity> hasDeleted(String isDeleted) {
        return (root, query, criteriaBuilder) -> {
            if (isDeleted == null || isDeleted.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(root.get("isDeleted").as(String.class), isDeleted.trim().toLowerCase() + "%");
        };
    }
}
