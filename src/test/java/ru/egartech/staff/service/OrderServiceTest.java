package ru.egartech.staff.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.egartech.staff.entity.*;
import ru.egartech.staff.entity.enums.Status;
import ru.egartech.staff.exception.StaffException;
import ru.egartech.staff.model.*;
import ru.egartech.staff.repository.OrderRepository;
import ru.egartech.staff.repository.ProductRepository;
import ru.egartech.staff.repository.StaffRepository;
import ru.egartech.staff.service.mapper.OrderMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    OrderRepository orderRepository;

    @Mock
    OrderMapper orderMapper;

    @Mock
    ProductRepository productRepository;

    @Mock
    StaffRepository staffRepository;

    @InjectMocks
    OrderService orderService;

    private OrderEntity orderEntity;
    private OrderListInfoResponseDto orderListInfoResponseDto;
    private OrderInfoResponseDto orderInfoResponseDto;
    private OrderSaveRequestDto orderSaveRequestDto;

    @BeforeEach
    void setUp() {
        orderEntity = new OrderEntity();
        orderEntity.setOrderDetails(new OrderDetailsEntity());
        orderListInfoResponseDto = new OrderListInfoResponseDto();
        orderInfoResponseDto = new OrderInfoResponseDto();
        orderSaveRequestDto = new OrderSaveRequestDto();
    }

    @Test
    void testGetAllNeededMaterialsInfo() {
        orderEntity.setId(1L);

        ProductEntity productEntity1 = createProductEntity(1L);
        ProductEntity productEntity2 = createProductEntity(2L);

        List<ProductEntity> productList = List.of(productEntity1, productEntity2);
        orderEntity.getOrderDetails().setProducts(productList);
        when(orderRepository.findById(orderEntity.getId())).thenReturn(Optional.of(orderEntity));

        OrderMaterialInfoResponseDto result = orderService.getAllNeededMaterialsInfo(orderEntity.getId());

        assertEquals(orderEntity.getId(), result.getId());
        assertEquals(2, result.getNeededMaterials().size());
        assertEquals(8, result.getNeededMaterials().get(0).getQuantity()); // Material 1: 3+5
        assertEquals(6, result.getNeededMaterials().get(1).getQuantity()); // Material 2: 6

        verify(orderRepository, times(1)).findById(orderEntity.getId());
    }

    private static ProductEntity createProductEntity(Long id){
        ProductEntity productEntity = new ProductEntity();
        productEntity.setId(id);
        List<ManualEntity> manualEntities = new ArrayList<>();
        for (int i = 1; i < id+1; i++) {
            ManualEntity manualEntity = new ManualEntity();
            ManualId manualId = new ManualId(id, (long) i);
            manualEntity.setId(manualId);
            manualEntity.setQuantity((int) (id * 2) + i);
            manualEntities.add(manualEntity);
        }
        productEntity.setManuals(manualEntities);
        return productEntity;
    }

    @Test
    void testGetAllOrders() {
        PageRequest pageRequest = PageRequest.of(1, 2, Sort.by(Sort.Direction.ASC, "id"));

        Page<OrderEntity> orderEntities = new PageImpl<>(List.of(orderEntity), pageRequest, 3);
        List<OrderListInfoResponseDto> orderDtos = List.of(orderListInfoResponseDto);

        when(orderRepository.findAll(pageRequest)).thenReturn(orderEntities);
        when(orderMapper.toListDto(orderEntities)).thenReturn(orderDtos);

        OrderInfoPagingResponseDto result = orderService.getAllOrders(1, 2, "asc", "id");

        assertEquals(1, result.getContent().size());
        assertEquals(2, result.getPaging().getPages());
        assertEquals(3, result.getPaging().getCount());
        assertEquals(1, result.getPaging().getPageNumber());
        assertEquals(2, result.getPaging().getPageSize());
        verify(orderRepository, times(1)).findAll(pageRequest);
    }

    @Test
    void testGetOrderById() {
        ProductEntity productEntity1 = new ProductEntity();
        productEntity1.setId(1L);
        ProductEntity productEntity2 = new ProductEntity();
        productEntity2.setId(2L);

        List<ProductEntity> productList = List.of(productEntity1, productEntity2);

        orderEntity.setId(1L);
        orderEntity.getOrderDetails().setProducts(productList);
        orderEntity.getOrderDetails().setStaff(List.of(new StaffEntity()));

        orderInfoResponseDto.setOrderProducts(List.of(new ProductShortInfoDto()));
        orderInfoResponseDto.setOrderStaff(List.of(new StaffShortInfoDto()));

        when(orderRepository.findById(orderEntity.getId())).thenReturn(Optional.of(orderEntity));
        when(orderMapper.toInfoResponseDto(orderEntity)).thenReturn(orderInfoResponseDto);
        when(orderMapper.toProductShortInfo(any())).thenReturn(List.of(
                new ProductShortInfoDto()
                        .id(1L)
                        .name("Product 1")
                        .price(BigDecimal.TEN.doubleValue())
                        .quantity(2),
                new ProductShortInfoDto()
                        .id(2L)
                        .name("Product 2")
                        .price(BigDecimal.TEN.doubleValue())
                        .quantity(1)
        ));
        when(orderMapper.toStaffShortInfo(orderEntity.getOrderDetails().getStaff())).thenReturn(List.of(new StaffShortInfoDto()));

        OrderInfoResponseDto result = orderService.getOrderById(orderEntity.getId());

        assertNotNull(result);
        assertEquals(2, result.getOrderProducts().size());
        assertEquals(1L, result.getOrderProducts().get(0).getId());
        assertEquals(2, result.getOrderProducts().get(0).getQuantity());
        assertEquals(2L, result.getOrderProducts().get(1).getId());
        assertEquals(1, result.getOrderProducts().get(1).getQuantity());
        assertNotNull(result.getOrderStaff());
        verify(orderRepository, times(1)).findById(orderEntity.getId());
        verify(orderMapper, times(1)).toInfoResponseDto(orderEntity);
        verify(orderMapper, times(1)).toProductShortInfo(any());
        verify(orderMapper, times(1)).toStaffShortInfo(orderEntity.getOrderDetails().getStaff());
    }


    @Test
    void testCreateOrder() {
        orderSaveRequestDto.setManagerId(1L);
        orderSaveRequestDto.setOrderProducts(List.of(1L, 2L));

        BigDecimal amount = new BigDecimal("100");
        ProductEntity product1 = new ProductEntity();
        product1.setId(1L);
        product1.setPrice(new BigDecimal("50"));
        ProductEntity product2 = new ProductEntity();
        product2.setId(2L);
        product2.setPrice(new BigDecimal("50"));

        List<StaffEntity> staffList = List.of(new StaffEntity());
        List<ProductEntity> products = List.of(product1, product2);

        when(staffRepository.findById(orderSaveRequestDto.getManagerId())).thenReturn(Optional.of(new StaffEntity()));
        when(productRepository.findById(orderSaveRequestDto.getOrderProducts().get(0))).thenReturn(Optional.of(product1));
        when(productRepository.findById(orderSaveRequestDto.getOrderProducts().get(1))).thenReturn(Optional.of(product2));
        when(orderMapper.toEntity(orderSaveRequestDto, orderEntity, staffList,  products, amount)).thenReturn(orderEntity);
        when(orderRepository.save(orderEntity)).thenReturn(orderEntity);

        orderService.createOrder(orderSaveRequestDto);

        verify(staffRepository, times(1)).findById(1L);
        verify(orderMapper, times(1)).toEntity(orderSaveRequestDto, orderEntity, staffList,  products, amount);
        verify(orderRepository, times(1)).save(orderEntity);
    }



    @Test
    void testOrderToNextStatus() {
        Long orderId = 1L;
        Long staffId = 1L;

        orderEntity.setId(orderId);
        orderEntity.getOrderDetails().setStatus(Status.ACCEPTED);
        orderEntity.getOrderDetails().setStaff(new ArrayList<>());
        StaffEntity staffEntity = new StaffEntity();
        staffEntity.setId(staffId);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderEntity));
        when(staffRepository.findById(staffId)).thenReturn(Optional.of(staffEntity));
        when(orderRepository.save(orderEntity)).thenReturn(orderEntity);

        orderService.orderToNextStatus(orderId, staffId);

        assertEquals(Status.PREPARATION, orderEntity.getOrderDetails().getStatus());
        verify(orderRepository, times(1)).findById(orderId);
        verify(staffRepository, times(1)).findById(staffId);
        verify(orderRepository, times(1)).save(orderEntity);
    }


    @Test
    void testOrderToPreparationStatus() {
        Long orderId = 1L;
        orderEntity.getOrderDetails().setStatus(Status.PACKAGING);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderEntity));

        orderService.orderToPreparationStatus(orderId);

        assertEquals(Status.PREPARATION, orderEntity.getOrderDetails().getStatus());
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(orderEntity);
    }

    @Test
    void testOrderToPreparationStatusInvalidStatus() {
        Long orderId = 1L;

        orderEntity.getOrderDetails().setStatus(Status.ACCEPTED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderEntity));

        StaffException exception = assertThrows(StaffException.class, () ->
                orderService.orderToPreparationStatus(orderId));

        assertEquals("Нельзя перезапустить заказ с этого состояния", exception.getMessage());
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, never()).save(orderEntity);
    }

    @Test
    void testDeleteOrderById() {
        Long orderId = 1L;

        orderService.deleteOrderById(orderId);

        verify(orderRepository, times(1)).deleteById(orderId);
    }
}
