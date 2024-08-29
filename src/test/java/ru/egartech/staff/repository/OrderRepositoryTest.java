package ru.egartech.staff.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.egartech.staff.entity.OrderEntity;
import ru.egartech.staff.entity.ProductEntity;
import ru.egartech.staff.entity.enums.ProductType;
import ru.egartech.staff.entity.enums.Status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@ActiveProfiles("test")
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    private OrderEntity order;
    private ProductEntity product;

    @BeforeEach
    void setUp() {
        order = new OrderEntity();
        order.setAddress("123 Test Street");
        order.setDate(LocalDate.now());
        order.setStatus(Status.ACCEPTED);
        order = orderRepository.save(order);

        product = new ProductEntity();
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setType(ProductType.OTHER);
        product.setPrice(new BigDecimal("99.99"));
        product = productRepository.save(product);
    }

    @Test
    void testAddProductToOrderProducts() {
        orderRepository.addProductToOrderProducts(product.getId(), order.getId());

        List<Long> products = orderRepository.findOrderProducts(order.getId());
        assertEquals(1, products.size());
        assertEquals(product.getId(), products.get(0));
    }

    @Test
    void testFindOrderProducts() {
        orderRepository.addProductToOrderProducts(product.getId(), order.getId());

        List<Long> products = orderRepository.findOrderProducts(order.getId());

        assertNotNull(products);
        assertEquals(1, products.size());
        assertEquals(product.getId(), products.get(0));
    }
}
