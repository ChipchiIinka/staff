package ru.egartech.staff.service.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.egartech.staff.entity.OrderEntity;
import ru.egartech.staff.entity.ProductEntity;
import ru.egartech.staff.entity.StaffEntity;
import ru.egartech.staff.entity.enums.Position;
import ru.egartech.staff.entity.enums.ProductType;
import ru.egartech.staff.entity.enums.Role;
import ru.egartech.staff.entity.enums.Status;
import ru.egartech.staff.model.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = OrderMapperImpl.class)
class OrderMapperTest {

    @Autowired
    private OrderMapper orderMapper;

    @Test
    void testToListDto(){
        OrderEntity orderEntity1 = createOrder(1L);
        OrderEntity orderEntity2 = createOrder(2L);

        List<OrderEntity> orderList = List.of(orderEntity1, orderEntity2);
        Page<OrderEntity> orderPage = new PageImpl<>(orderList, PageRequest.of(0, 10), orderList.size());

        List<OrderListInfoResponseDto> result = orderMapper.toListDto(orderPage);

        assertEquals(2, result.size());
        assertEquals("Город N, ул.Уличная, 1", result.get(0).getAddress());
        assertEquals("Город N, ул.Уличная, 2", result.get(1).getAddress());
        assertEquals(LocalDate.now(), result.get(0).getDate());
        assertEquals(LocalDate.now(), result.get(1).getDate());
        assertEquals(OrderStatusDto.ACCEPTED, result.get(0).getStatus());
        assertEquals(OrderStatusDto.ACCEPTED, result.get(1).getStatus());
    }

    @Test
    void testToDto(){
        OrderEntity orderEntity1 = createOrder(1L);

        OrderListInfoResponseDto result = orderMapper.toDto(orderEntity1);

        assertEquals("Город N, ул.Уличная, 1", result.getAddress());
        assertEquals(OrderStatusDto.ACCEPTED, result.getStatus());
        assertEquals(LocalDate.now(), result.getDate());
    }

    @Test
    void testToInfoResponseDto(){
        OrderEntity orderEntity = createOrder(1L);

        OrderInfoResponseDto result = orderMapper.toInfoResponseDto(orderEntity);

        assertEquals(1L, result.getId());
        assertEquals("Город N, ул.Уличная, 1", result.getAddress());
        assertEquals(OrderStatusDto.ACCEPTED, result.getStatus());
        assertEquals(List.of(1L, 2L), result.getOrderProducts());
        assertEquals(List.of(1L, 2L), result.getOrderProducts());
        assertEquals(LocalDate.now(), result.getDate());
    }

    @Test
    void testToEntity() {
        OrderSaveRequestDto requestDto = new OrderSaveRequestDto();
        requestDto.setCity("Город N");
        requestDto.setStreet("ул.Уличная");
        requestDto.setHouse(String.valueOf(1L));
        requestDto.setOrderProducts(List.of(1L, 2L, 3L));

        StaffEntity staffEntity = getStaffEntity(0);

        OrderEntity result = new OrderEntity();
        result.setId(1L);
        result.setStaff(new ArrayList<>());

        orderMapper.toEntity(staffEntity, requestDto, result);

        assertNotNull(result);
        assertEquals("Город N, ул.Уличная, 1", result.getAddress());
        assertEquals(Status.ACCEPTED, result.getStatus());
        assertEquals(LocalDate.now(), result.getDate());
    }

    @Test
    void testToManualInfoDto(){
        Map<Long, Integer> map = new HashMap<>();
        map.put(1L, 2);
        map.put(2L, 12);
        map.put(3L, 10);

        List<ManualInfoResponseDto> result = orderMapper.toManualInfoDto(map);
        assertEquals(1L, result.get(0).getId());
        assertEquals(2, result.get(0).getQuantity());
        assertEquals(2L, result.get(1).getId());
        assertEquals(12, result.get(1).getQuantity());
        assertEquals(3L, result.get(2).getId());
        assertEquals(10, result.get(2).getQuantity());
    }


    private static OrderEntity createOrder(Long id) {
        List<ProductEntity> products = new ArrayList<>();
        for (int i = 0; i < 2; i++){
            products.add(getProductEntity(i));
        }
        List<StaffEntity> staff = new ArrayList<>();
        for (int i = 0; i < 2; i++){
            staff.add(getStaffEntity(i));
        }
        OrderEntity order = new OrderEntity();
        order.setId(id);
        order.setAddress("Город N, ул.Уличная, " + id);
        order.setStatus(Status.ACCEPTED);
        order.setDate(LocalDate.now());
        order.setProducts(products);
        order.setStaff(staff);
        return order;
    }

    private static ProductEntity getProductEntity(int i) {
        ProductEntity product = new ProductEntity();
        long productId = i + 1;
        product.setId(productId);
        product.setName("Товар " + productId);
        product.setDescription("Описание товара " + product);
        product.setPrice(BigDecimal.valueOf(productId * 100));
        product.setType(ProductType.OTHER);
        return product;
    }

    private static StaffEntity getStaffEntity(int i) {
        StaffEntity staffEntity = new StaffEntity();
        long staffId = i + 1;
        staffEntity.setId(staffId);
        staffEntity.setLogin("login" + staffId);
        staffEntity.setFullName("Фамилия Имя Отчество");
        staffEntity.setPassword("password" + staffId);
        staffEntity.setPhoneNumber("+12903234212" + staffId);
        staffEntity.setEmail(String.format("email%d@example.com", staffId));
        staffEntity.setRole(Role.ADMIN);
        staffEntity.setPosition(Position.DIRECTOR);
        staffEntity.setDeleted(false);
        return staffEntity;
    }
}