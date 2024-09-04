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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    void testCreate(){
        assertNotNull(productRepository.findById(product.getId()));
        assertEquals(product.getName(), productRepository.findById(product.getId()).get().getName());
        assertEquals(product.getDescription(), productRepository.findById(product.getId()).get().getDescription());
        assertEquals(product.getPrice(), productRepository.findById(product.getId()).get().getPrice());
        assertNotNull(material);
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
