package ru.egartech.staff.service.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.egartech.staff.entity.OrderDetailsEntity;
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
import java.util.*;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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
        assertFalse(result.get(0).getIsPaid());
        assertFalse(result.get(1).getIsPaid());
        assertEquals(300.0, result.get(0).getAmount());
        assertEquals(300.0, result.get(1).getAmount());
        assertEquals(OrderStatusDto.ACCEPTED, result.get(0).getStatus());
        assertEquals(OrderStatusDto.ACCEPTED, result.get(1).getStatus());
    }

    @Test
    void testToDto(){
        OrderEntity orderEntity1 = createOrder(1L);

        OrderListInfoResponseDto result = orderMapper.toDto(orderEntity1);

        assertEquals("Город N, ул.Уличная, 1", result.getAddress());
        assertEquals(OrderStatusDto.ACCEPTED, result.getStatus());
        assertFalse(result.getIsPaid());
        assertEquals(300.0, result.getAmount());
        assertEquals(LocalDate.now(), result.getDate());
    }

    @Test
    void testToInfoResponseDto(){
        OrderEntity orderEntity = createOrder(1L);

        OrderInfoResponseDto result = orderMapper.toInfoResponseDto(orderEntity);

        assertEquals(1L, result.getId());
        assertEquals("Город N, ул.Уличная, 1", result.getAddress());
        assertEquals(OrderStatusDto.ACCEPTED, result.getStatus());
        assertEquals(300.d, result.getAmount());
        assertFalse(result.getIsPaid());
        assertEquals("Description", result.getDescription());
        assertEquals(LocalDate.now(), result.getDate());
    }

    @Test
    void testToEntity() {
        OrderSaveRequestDto requestDto = new OrderSaveRequestDto();
        requestDto.setCity("Город N");
        requestDto.setStreet("ул.Уличная");
        requestDto.setHouse(String.valueOf(1L));
        requestDto.setOrderProducts(List.of(1L, 2L, 3L));
        requestDto.setDescription("Description");

        List<StaffEntity> staffList = List.of(getStaffEntity(0));
        List<ProductEntity> productList = List.of(getProductEntity(0));

        OrderEntity result = new OrderEntity();
        result.setId(1L);
        result.setOrderDetails(new OrderDetailsEntity());

        BigDecimal amount = new BigDecimal("300.00");

        orderMapper.toEntity(requestDto, result, staffList, productList, amount);

        assertNotNull(result);
        assertEquals("Город N, ул.Уличная, 1", result.getAddress());
        assertEquals(LocalDate.now(), result.getDate());
        assertFalse(result.getOrderDetails().isPaid());
        assertEquals(Status.ACCEPTED, result.getOrderDetails().getStatus());
        assertEquals("Description", result.getOrderDetails().getDescription());
        assertEquals(List.of(getProductEntity(0)), result.getOrderDetails().getProducts());
        assertEquals(List.of(getStaffEntity(0)), result.getOrderDetails().getStaff());
    }

    @Test
    void testToShortInfo(){
        Map<ProductEntity, Long> products = new HashMap<>();
        for (int i = 0; i < 2; i++){
            products.put(getProductEntity(i), 1L);
        }
        List<StaffEntity> staff = new ArrayList<>();
        for (int i = 0; i < 2; i++){
            staff.add(getStaffEntity(i));
        }

        List<ProductShortInfoDto> productResult = orderMapper.toProductShortInfo(products);
        List<StaffShortInfoDto> staffResult = orderMapper.toStaffShortInfo(staff);


        assertEquals(2, productResult.size());
        assertEquals(2, staffResult.size());
        assertThat(productResult).containsExactlyInAnyOrder(getProductShortInfo(0), getProductShortInfo(1));
        assertEquals(List.of(getStaffShortInfo(0), getStaffShortInfo(1)), staffResult);
    }

    @Test
    void testToFullAddress(){
        OrderSaveRequestDto requestDto = new OrderSaveRequestDto();
        requestDto.setCity("city");
        requestDto.setStreet("street");
        requestDto.setHouse("1");

        String fullAddress = orderMapper.toFullAddress(requestDto);

        assertEquals("city, street, 1", fullAddress);
    }

    @Test
    void mapStatus(){
        OrderStatusDto statusDto1 = orderMapper.mapStatus(Status.ACCEPTED);
        OrderStatusDto statusDto2 = orderMapper.mapStatus(Status.PREPARATION);
        OrderStatusDto statusDto3 = orderMapper.mapStatus(Status.ASSEMBLY);
        OrderStatusDto statusDto4 = orderMapper.mapStatus(Status.PACKAGING);
        OrderStatusDto statusDto5 = orderMapper.mapStatus(Status.DELIVERY);
        OrderStatusDto statusDto6 = orderMapper.mapStatus(Status.COMPLETED);
        OrderStatusDto statusDto7 = orderMapper.mapStatus(Status.CANCELED);

        assertEquals(OrderStatusDto.ACCEPTED, statusDto1);
        assertEquals(OrderStatusDto.PREPARATION, statusDto2);
        assertEquals(OrderStatusDto.ASSEMBLY, statusDto3);
        assertEquals(OrderStatusDto.PACKAGING, statusDto4);
        assertEquals(OrderStatusDto.DELIVERY, statusDto5);
        assertEquals(OrderStatusDto.COMPLETED, statusDto6);
        assertEquals(OrderStatusDto.CANCELED, statusDto7);
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
        order.setDate(LocalDate.now());
        order.setAmount(BigDecimal.valueOf(300.00));
        order.setOrderDetails(new OrderDetailsEntity());
        order.getOrderDetails().setStatus(Status.ACCEPTED);
        order.getOrderDetails().setDescription("Description");
        order.getOrderDetails().setPaid(false);
        order.getOrderDetails().setProducts(products);
        order.getOrderDetails().setStaff(staff);
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

    private static ProductShortInfoDto getProductShortInfo(int i) {
        ProductShortInfoDto product = new ProductShortInfoDto();
        long productId = i + 1;
        product.setId(productId);
        product.setName("Товар " + productId);
        product.setPrice(BigDecimal.valueOf(productId * 100).doubleValue());
        product.quantity(1);
        return product;
    }

    private static StaffShortInfoDto getStaffShortInfo(int i) {
        StaffShortInfoDto staff = new StaffShortInfoDto();
        long staffId = i + 1;
        staff.setId(staffId);
        staff.setFullName("Фамилия Имя Отчество");
        staff.setPosition(StaffPositionDto.DIRECTOR);
        return staff;
    }
}