package ru.egartech.staff.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.egartech.staff.entity.StorageEntity;

import java.util.List;

@Repository
public interface StoragesRepository extends JpaRepository<StorageEntity, Long> {

    // Запрос для нахождения доступного количества материалов по их ID
    @Query(value = "SELECT SUM(ms.available) FROM materials_storage ms WHERE ms.material_id = :#{#materialId}", nativeQuery = true)
    Long findAvailableByMaterialId(@Param("materialId") Long materialId);

    // Запрос для нахождения доступного количества продуктов по их ID
    @Query(value = "SELECT SUM(ps.available) FROM products_storage ps WHERE ps.product_id = :#{#productId}", nativeQuery = true)
    Long findAvailableByProductId(@Param("productId") Long productId);

    @Query(value = "SELECT ps.product_id, ps.available " +
            "FROM products_storage as ps " +
            "WHERE ps.storage_id = :#{#storageId}", nativeQuery = true)
    List<Object[]> findAllProductsByStorageId(@Param("storageId") Long storageId);

    @Query(value = "SELECT ms.material_id, ms.available " +
            "FROM materials_storage as ms " +
            "WHERE ms.storage_id = :#{#storageId}", nativeQuery = true)
    List<Object[]> findAllMaterialsByStorageId(@Param("storageId") Long storageId);
}
