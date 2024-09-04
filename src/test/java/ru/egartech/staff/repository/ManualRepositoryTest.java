package ru.egartech.staff.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.egartech.staff.entity.ManualEntity;
import ru.egartech.staff.entity.ManualId;
import ru.egartech.staff.entity.MaterialEntity;
import ru.egartech.staff.entity.ProductEntity;
import ru.egartech.staff.entity.enums.MaterialType;
import ru.egartech.staff.entity.enums.ProductType;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
class ManualRepositoryTest {

    @Autowired
    private ManualRepository manualRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private EntityManager em;

    private MaterialEntity material;
    private ProductEntity product;
    private ManualEntity manual;

    @BeforeEach
    void setUp() {
        material = new MaterialEntity();
        material.setName("Material");
        material.setDescription("Material Description");
        material.setWidth(1);
        material.setLength(2);
        material.setHeight(3);
        material.setType(MaterialType.OTHER);
        material = materialRepository.save(material);

        product = new ProductEntity();
        product.setName("Product");
        product.setDescription("Product Description");
        product.setPrice(BigDecimal.valueOf(100));
        product.setType(ProductType.OTHER);
        product = productRepository.save(product);

        manual = new ManualEntity();
        manual.setId(new ManualId(product.getId(), material.getId()));
        manual.setProduct(product);
        manual.setMaterial(material);
        manual.setQuantity(1);
        manual = manualRepository.save(manual);

        em.flush();
        em.clear();
    }

    @Test
    void deleteAllManuals(){
        ProductEntity dbProduct = productRepository.findById(product.getId()).orElse(null);
        assertEquals(product.getId(), manual.getProduct().getId());
        assertEquals(material.getId(), manual.getMaterial().getId());
        assert dbProduct != null;
        assertEquals(dbProduct.getManuals().get(0).getQuantity(), manual.getQuantity());

        manualRepository.deleteAllByProduct(product);
        assertEquals(List.of(), product.getManuals());
    }
}
