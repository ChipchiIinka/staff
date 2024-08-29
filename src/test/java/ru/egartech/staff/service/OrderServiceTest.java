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
import ru.egartech.staff.entity.OrderEntity;
import ru.egartech.staff.entity.StaffEntity;
import ru.egartech.staff.entity.enums.Status;
import ru.egartech.staff.exception.StaffException;
import ru.egartech.staff.model.*;
import ru.egartech.staff.repository.OrderRepository;
import ru.egartech.staff.repository.StaffRepository;
import ru.egartech.staff.service.mapper.OrderMapper;

import java.util.List;
import java.util.Map;
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
    ProductService productService;

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
        orderListInfoResponseDto = new OrderListInfoResponseDto();
        orderInfoResponseDto = new OrderInfoResponseDto();
        orderSaveRequestDto = new OrderSaveRequestDto();
    }

    @Test
    void testGetAllNeededMaterialsInfo() {
        Long orderId = 1L;
        List<Long> productList = List.of(100L, 101L);

        when(orderRepository.findOrderProducts(orderId)).thenReturn(productList);
        when(productService.getManualMapInfo(100L)).thenReturn(Map.of(1L, 5));
        when(productService.getManualMapInfo(101L)).thenReturn(Map.of(1L, 2, 2L, 3));

        // Для первого продукта (ID = 100L)
        when(orderMapper.toManualInfoDto(Map.of(1L, 5))).thenReturn(List.of(
                new ManualInfoResponseDto().id(1L).quantity(5)));
        // Для второго продукта (ID = 101L)
        when(orderMapper.toManualInfoDto(Map.of(1L, 2, 2L, 3))).thenReturn(List.of(
                new ManualInfoResponseDto().id(1L).quantity(2), new ManualInfoResponseDto().id(2L).quantity(3)));

        OrderMaterialInfoResponseDto result = orderService.getAllNeededMaterialsInfo(orderId);

        assertEquals(orderId, result.getId());
        assertEquals(2, result.getNeededMaterials().size());
        assertEquals(7, result.getNeededMaterials().get(0).getQuantity()); // Manual A (5+2)
        assertEquals(3, result.getNeededMaterials().get(1).getQuantity()); // Manual B
        verify(orderRepository, times(1)).findOrderProducts(orderId);
        verify(productService, times(1)).getManualMapInfo(100L);
        verify(productService, times(1)).getManualMapInfo(101L);
        verify(orderMapper, times(1)).toManualInfoDto(Map.of(1L, 5));
        verify(orderMapper, times(1)).toManualInfoDto(Map.of(1L, 2, 2L, 3));
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
        Long orderId = 1L;

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderEntity));
        when(orderMapper.toInfoResponseDto(orderEntity)).thenReturn(orderInfoResponseDto);

        OrderInfoResponseDto result = orderService.getOrderById(orderId);

        assertNotNull(result);
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderMapper, times(1)).toInfoResponseDto(orderEntity);
    }

    @Test
    void testCreateOrder() {
        orderSaveRequestDto.setManagerId(1L);
        orderSaveRequestDto.setOrderProducts(List.of(1L, 2L));

        StaffEntity manager = new StaffEntity();

        when(staffRepository.findById(1L)).thenReturn(Optional.of(manager));
        when(orderMapper.toEntity(manager, orderSaveRequestDto, orderEntity)).thenReturn(orderEntity);
        when(orderRepository.save(orderEntity)).thenReturn(orderEntity);

        orderService.createOrder(orderSaveRequestDto);

        verify(staffRepository, times(1)).findById(1L);
        verify(orderMapper, times(1)).toEntity(manager, orderSaveRequestDto, orderEntity);
        verify(orderRepository, times(1)).save(orderEntity);
    }



    @Test
    void testOrderToNextStatus() {
        Long orderId = 1L;
        orderEntity.setStatus(Status.ACCEPTANCE);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderEntity));

        orderService.orderToNextStatus(orderId);

        assertEquals(Status.ACCEPTED, orderEntity.getStatus());
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(orderEntity);
    }

    @Test
    void testOrderToPreparationStatus() {
        Long orderId = 1L;
        orderEntity.setStatus(Status.PACKAGING);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderEntity));

        orderService.orderToPreparationStatus(orderId);

        assertEquals(Status.PREPARATION, orderEntity.getStatus());
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(orderEntity);
    }

    @Test
    void testOrderToPreparationStatusInvalidStatus() {
        Long orderId = 1L;
        orderEntity.setStatus(Status.ACCEPTANCE);

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
