package ru.egartech.staff.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.egartech.staff.entity.StaffEntity;

@Repository
public interface StaffRepository extends JpaRepository<StaffEntity, Long> {

    @Modifying
    @Query(value = "UPDATE StaffEntity s SET s.isDeleted = true WHERE s.id = :staffId")
    void markAsBanned(@Param("staffId") Long staffId);

    @Modifying
    @Query(value = "UPDATE StaffEntity s SET s.isDeleted = false WHERE s.id = :staffId")
    void markAsUnbanned(@Param("staffId") Long staffId);
}
