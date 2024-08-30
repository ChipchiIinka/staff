package ru.egartech.staff.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.egartech.staff.entity.OrderEntity;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    @Modifying(clearAutomatically = true)
    @Query(value = "INSERT INTO order_products (order_id, product_id) VALUES (:orderId, :productId)", nativeQuery = true)
    void addProductToOrderProducts(@Param("productId") Long productId, @Param("orderId") Long orderId);

    @Query(value = "SELECT product_id FROM order_products WHERE order_id = :orderId", nativeQuery = true)
    List<Long> findOrderProducts(@Param("orderId") Long orderId);

    @Modifying
    @Query(value = "INSERT INTO staff_projects (order_id, staff_id) VALUES (:orderId, :staffId) ON CONFLICT DO NOTHING", nativeQuery = true)
    void addToOrderStaff(@Param("orderId") Long orderId, @Param("staffId") Long staffId);
}
