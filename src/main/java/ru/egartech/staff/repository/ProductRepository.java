package ru.egartech.staff.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.egartech.staff.entity.ProductEntity;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    @Query(value = "SELECT m.id, mn.quantity " +
            "FROM materials m " +
            "JOIN manual mn ON m.id = mn.material_id " +
            "JOIN products p ON p.id = mn.product_id " +
            "WHERE p.id = :productId", nativeQuery = true)
    List<Object[]> findManualMapInfoMaterialQuantity(@Param("productId") Long productId);

    @Modifying
    @Query(value = "INSERT INTO manual (product_id, material_id, quantity) " +
            "VALUES (:productId, :materialId, :quantity)", nativeQuery = true)
    void saveManual(@Param("productId") Long productId,
                    @Param("materialId") Long materialId,
                    @Param("quantity") Integer quantity);

    @Modifying
    @Query(value = "DELETE FROM manual WHERE product_id = :productId", nativeQuery = true)
    void deleteAllManualByProductId(@Param("productId") Long productId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM manual WHERE product_id = :productId;" +
            " DELETE FROM products WHERE id = :productId", nativeQuery = true)
    void deleteProductById(@Param("productId") Long productId);
}
