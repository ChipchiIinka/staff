package ru.egartech.staff.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.egartech.staff.entity.StaffEntity;

@Repository
public interface StaffRepository extends JpaRepository<StaffEntity, Long> {

    @Modifying
    @Transactional
    @Query(value = "UPDATE staff SET is_deleted = true WHERE id = :staffId", nativeQuery = true)
    void markAsBanned(@Param("staffId") Long staffId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE staff SET is_deleted = false WHERE id = :staffId", nativeQuery = true)
    void markAsUnbanned(@Param("staffId") Long staffId);
}
