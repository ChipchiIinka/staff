package ru.egartech.staff.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.egartech.staff.entity.OrderEntity;
import ru.egartech.staff.entity.enums.Status;

import java.time.LocalDate;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long>, JpaSpecificationExecutor<OrderEntity> {

    @Modifying
    @Query("DELETE FROM OrderEntity o WHERE o.date < :thresholdDate AND o.orderDetails.status = :status")
    void deleteExpiredCanceledOrders(@Param("thresholdDate") LocalDate thresholdDate, @Param("status") Status status);
}
