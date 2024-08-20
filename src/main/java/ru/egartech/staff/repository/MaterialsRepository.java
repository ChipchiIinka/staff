package ru.egartech.staff.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.egartech.staff.entity.MaterialEntity;

@Repository
public interface MaterialsRepository extends JpaRepository<MaterialEntity, Long> {

}
