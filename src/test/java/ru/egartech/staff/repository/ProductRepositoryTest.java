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

import java.math.BigDecimal;
import java.util.List;

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

        List<Object[]> manualMapInfo = productRepository.findManualMapInfoMaterialQuantity(product.getId());
        assertEquals(1, manualMapInfo.size());

        Object[] manualEntry = manualMapInfo.get(0);
        assertEquals(material.getId(), manualEntry[0]);
        assertEquals(10, manualEntry[1]);
    }

    @Test
    void testFindManualMapInfoMaterialQuantity() {
        productRepository.saveManual(product.getId(), material.getId(), 10);

        List<Object[]> manualMapInfo = productRepository.findManualMapInfoMaterialQuantity(product.getId());

        assertNotNull(manualMapInfo);
        assertEquals(1, manualMapInfo.size());

        Object[] manualEntry = manualMapInfo.get(0);
        assertEquals(material.getId(), manualEntry[0]);
        assertEquals(10, manualEntry[1]);
    }

    @Test
    void testDeleteAllManualByProductId() {
        productRepository.saveManual(product.getId(), material.getId(), 10);

        productRepository.deleteAllManualByProductId(product.getId());

        List<Object[]> manualMapInfo = productRepository.findManualMapInfoMaterialQuantity(product.getId());
        assertTrue(manualMapInfo.isEmpty());
    }
}
