package ru.egartech.staff.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.egartech.staff.entity.OrderEntity;

import java.util.List;

@Repository
public interface OrdersRepository extends JpaRepository<OrderEntity, Long> {

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "insert into order_products (order_id, product_id) values (:#{#orderId}, :#{#productId})", nativeQuery = true)
    void addProductToOrderProducts(@Param("productId") Long productId, @Param("orderId") Long orderId);

    @Modifying
    @Query(value = "select product_id from order_products where order_id = :#{#orderId}", nativeQuery = true)
    List<Long> findOrderProducts(@Param("orderId") Long orderId);
}
