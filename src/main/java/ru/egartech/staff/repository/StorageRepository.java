package ru.egartech.staff.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.egartech.staff.entity.StorageEntity;

import java.util.List;

@Repository
public interface StorageRepository extends JpaRepository<StorageEntity, Long> {

    // Запрос для нахождения доступного количества материалов по их ID
    @Query(value = "SELECT SUM(ms.available) FROM materials_storage ms WHERE ms.material_id = :materialId", nativeQuery = true)
    Long findAvailableByMaterialId(@Param("materialId") Long materialId);

    // Запрос для нахождения доступного количества продуктов по их ID
    @Query(value = "SELECT SUM(ps.available) FROM products_storage ps WHERE ps.product_id = :productId", nativeQuery = true)
    Long findAvailableByProductId(@Param("productId") Long productId);

    @Query(value = "SELECT ps.product_id, ps.available " +
            "FROM products_storage AS ps " +
            "WHERE ps.storage_id = :storageId", nativeQuery = true)
    List<Object[]> findAllProductsByStorageId(@Param("storageId") Long storageId);

    @Query(value = "SELECT ms.material_id, ms.available " +
            "FROM materials_storage AS ms " +
            "WHERE ms.storage_id = :storageId", nativeQuery = true)
    List<Object[]> findAllMaterialsByStorageId(@Param("storageId") Long storageId);

    @Modifying
    @Query(value = "INSERT INTO materials_storage(material_id, available, storage_id) " +
            "VALUES (:materialId, :available, :storageId) " +
            "ON CONFLICT (material_id, storage_id) DO UPDATE " +
            "SET available = :available", nativeQuery = true)
    void addMaterialToStorage(@Param("storageId") Long storageId,
                              @Param("materialId") Long materialId,
                              @Param("available") Integer available);

    @Modifying
    @Query(value = "INSERT INTO products_storage(product_id, available, storage_id) " +
            "VALUES (:productId, :available, :storageId) " +
            "ON CONFLICT (product_id, storage_id) DO UPDATE " +
            "SET available = :available", nativeQuery = true)
    void addProductToStorage(@Param("storageId") Long storageId,
                             @Param("productId") Long productId,
                             @Param("available") Integer available);
}
