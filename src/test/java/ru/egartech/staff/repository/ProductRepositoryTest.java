package ru.egartech.staff.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.egartech.staff.entity.MaterialEntity;
import ru.egartech.staff.entity.ProductEntity;
import ru.egartech.staff.entity.enums.MaterialType;
import ru.egartech.staff.entity.enums.ProductType;
import ru.egartech.staff.entity.projection.ManualProjection;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MaterialRepository materialRepository;

    private ProductEntity product;
    private MaterialEntity material;

    @BeforeEach
    void setUp() {
        product = new ProductEntity();
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setType(ProductType.OTHER);
        product.setPrice(new BigDecimal("99.99"));
        product = productRepository.save(product);

        material = new MaterialEntity();
        material.setName("Test Material");
        material.setDescription("Test Material Description");
        material.setLength(10);
        material.setWidth(5);
        material.setHeight(3);
        material.setType(MaterialType.METAL);
        material = materialRepository.save(material);
    }

    @Test
    void testSaveManual() {
        productRepository.saveManual(product.getId(), material.getId(), 10);

        List<ManualProjection> manualProjections = productRepository.findProductManualProjection(product.getId());
        assertEquals(1, manualProjections.size());

        ManualProjection manualProjection = manualProjections.get(0);
        assertEquals(material.getId(), manualProjection.getMaterial());
        assertEquals(10, manualProjection.getQuantity());
    }

    @Test
    void testFindManualMapInfoMaterialQuantity() {
        productRepository.saveManual(product.getId(), material.getId(), 10);

        List<ManualProjection> manualProjection = productRepository.findProductManualProjection(product.getId());

        assertNotNull(manualProjection);
        assertEquals(1, manualProjection.size());

        ManualProjection manualProjection1 = manualProjection.get(0);
        assertEquals(material.getId(), manualProjection1.getMaterial());
        assertEquals(10, manualProjection1.getQuantity());
    }

    @Test
    void testDeleteAllManualByProductId() {
        productRepository.saveManual(product.getId(), material.getId(), 10);

        productRepository.deleteAllManualByProductId(product.getId());

        List<ManualProjection> manualProjection = productRepository.findProductManualProjection(product.getId());
        assertTrue(manualProjection.isEmpty());
    }

    @Test
    void testDeleteById() {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setName("Test Product");
        productEntity.setDescription("Test Description");
        productEntity.setType(ProductType.OTHER);
        productEntity.setPrice(new BigDecimal("99.99"));
        productEntity = productRepository.save(productEntity);

        assertEquals(Optional.of(productEntity), productRepository.findById(productEntity.getId()));
        productRepository.deleteById(productEntity.getId());

        assertEquals(Optional.empty(), productRepository.findById(productEntity.getId()));
    }
}
