package ru.egartech.staff.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.egartech.staff.entity.OrderDetailsEntity;
import ru.egartech.staff.entity.OrderEntity;
import ru.egartech.staff.entity.ProductEntity;
import ru.egartech.staff.entity.StaffEntity;
import ru.egartech.staff.entity.enums.Position;
import ru.egartech.staff.entity.enums.ProductType;
import ru.egartech.staff.entity.enums.Role;
import ru.egartech.staff.entity.enums.Status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private ProductRepository productRepository;

    private OrderEntity order;
    private StaffEntity staff;
    private ProductEntity product1;
    private ProductEntity product2;

    @BeforeEach
    void setUp() {
        order = new OrderEntity();
        order.setAddress("123 Test Street");
        order.setDate(LocalDate.now());
        order.setAmount(new BigDecimal("100.00"));
        order.setOrderDetails(new OrderDetailsEntity());
        order.getOrderDetails().setStatus(Status.ACCEPTED);
        order.getOrderDetails().setDescription("Description");

        staff = new StaffEntity();
        staff.setLogin("coolestLogin228");
        staff.setPassword("qwerty123");
        staff.setFullName("John Doe Doewich");
        staff.setPhoneNumber("+89825227232");
        staff.setEmail("email@email.com");
        staff.setRole(Role.USER);
        staff.setPosition(Position.MANAGER);
        staff = staffRepository.save(staff);

        order.getOrderDetails().setStaff(List.of(staff));

        product1 = new ProductEntity();
        product1.setName("Test Product1");
        product1.setDescription("Test Description1");
        product1.setType(ProductType.OTHER);
        product1.setPrice(new BigDecimal("99.99"));
        product1 = productRepository.save(product1);

        product2 = new ProductEntity();
        product2.setName("Test Product2");
        product2.setDescription("Test Description2");
        product2.setType(ProductType.OTHER);
        product2.setPrice(new BigDecimal("00.01"));
        product2 = productRepository.save(product2);

        order.getOrderDetails().setProducts(List.of(product1, product2));
        order = orderRepository.save(order);
    }

    @Test
    void testOrderSave(){
        assertEquals("123 Test Street", order.getAddress());
        assertEquals("Description", order.getOrderDetails().getDescription());
        assertEquals(LocalDate.now(), order.getDate());
        assertEquals(Status.ACCEPTED, order.getOrderDetails().getStatus());
        assertEquals(new BigDecimal("100.00"), order.getAmount());
        assertEquals(staff, order.getOrderDetails().getStaff().get(0));
        assertEquals(product1, order.getOrderDetails().getProducts().get(0));
        assertEquals(product2, order.getOrderDetails().getProducts().get(1));
    }
}
