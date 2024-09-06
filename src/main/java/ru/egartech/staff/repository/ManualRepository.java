package ru.egartech.staff.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.egartech.staff.entity.ManualEntity;
import ru.egartech.staff.entity.ManualId;
import ru.egartech.staff.entity.ProductEntity;

@Repository
public interface ManualRepository extends JpaRepository<ManualEntity, ManualId> {
    void deleteAllByProduct(ProductEntity product);
}
