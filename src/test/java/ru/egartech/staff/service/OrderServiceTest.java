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
import ru.egartech.staff.entity.ProductEntity;
import ru.egartech.staff.entity.StaffEntity;
import ru.egartech.staff.entity.enums.Status;
import ru.egartech.staff.entity.projection.ManualProjection;
import ru.egartech.staff.exception.StaffException;
import ru.egartech.staff.model.*;
import ru.egartech.staff.repository.OrderRepository;
import ru.egartech.staff.repository.ProductRepository;
import ru.egartech.staff.repository.StaffRepository;
import ru.egartech.staff.service.mapper.OrderMapper;

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
        orderListInfoResponseDto = new OrderListInfoResponseDto();
        orderInfoResponseDto = new OrderInfoResponseDto();
        orderSaveRequestDto = new OrderSaveRequestDto();
    }

    @Test
    void testGetAllNeededMaterialsInfo() {
        Long orderId = 1L;

        ProductEntity productEntity1 = new ProductEntity();
        productEntity1.setId(1L);
        ProductEntity productEntity2 = new ProductEntity();
        productEntity2.setId(2L);

        List<ProductEntity> productList = List.of(productEntity1, productEntity2);

        List<ManualProjection> manualProjectionList1 = List.of(
                new ManualProjection() {
                    @Override
                    public Long getMaterial() {
                        return 1L;
                    }

                    @Override
                    public Integer getQuantity() {
                        return 5;
                    }
                }
        );

        List<ManualProjection> manualProjectionList2 = List.of(
                new ManualProjection() {
                    @Override
                    public Long getMaterial() {
                        return 1L;
                    }

                    @Override
                    public Integer getQuantity() {
                        return 2;
                    }
                },
                new ManualProjection() {
                    @Override
                    public Long getMaterial() {
                        return 2L;
                    }

                    @Override
                    public Integer getQuantity() {
                        return 3;
                    }
                }
        );

        when(orderRepository.findOrderProducts(orderId)).thenReturn(productList);
        when(productRepository.findProductManualProjection(productEntity1.getId())).thenReturn(manualProjectionList1);
        when(productRepository.findProductManualProjection(productEntity2.getId())).thenReturn(manualProjectionList2);

        OrderMaterialInfoResponseDto result = orderService.getAllNeededMaterialsInfo(orderId);

        assertEquals(orderId, result.getId());
        assertEquals(2, result.getNeededMaterials().size());
        assertEquals(7, result.getNeededMaterials().get(0).getQuantity()); // Manual A (5+2)
        assertEquals(3, result.getNeededMaterials().get(1).getQuantity()); // Manual B
        verify(orderRepository, times(1)).findOrderProducts(orderId);
        verify(productRepository, times(1)).findProductManualProjection(productEntity1.getId());
        verify(productRepository, times(1)).findProductManualProjection(productEntity2.getId());
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

        List<StaffEntity> staffList = List.of(new StaffEntity());
        List<ProductEntity> products = List.of(new ProductEntity(), new ProductEntity());

        when(staffRepository.findById(orderSaveRequestDto.getManagerId())).thenReturn(Optional.of(new StaffEntity()));
        when(productRepository.findById(orderSaveRequestDto.getOrderProducts().get(0))).thenReturn(Optional.of(new ProductEntity()));
        when(productRepository.findById(orderSaveRequestDto.getOrderProducts().get(1))).thenReturn(Optional.of(new ProductEntity()));
        when(orderMapper.toEntity(staffList, orderSaveRequestDto, orderEntity, products)).thenReturn(orderEntity);
        when(orderRepository.save(orderEntity)).thenReturn(orderEntity);

        orderService.createOrder(orderSaveRequestDto);

        verify(staffRepository, times(1)).findById(1L);
        verify(orderMapper, times(1)).toEntity(staffList, orderSaveRequestDto, orderEntity, products);
        verify(orderRepository, times(1)).save(orderEntity);
    }



    @Test
    void testOrderToNextStatus() {
        Long orderId = 1L;
        Long staffId = 1L;

        orderEntity.setId(orderId);
        orderEntity.setStatus(Status.ACCEPTED);
        orderEntity.setStaff(new ArrayList<>());
        StaffEntity staffEntity = new StaffEntity();
        staffEntity.setId(staffId);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderEntity));
        when(staffRepository.findById(staffId)).thenReturn(Optional.of(staffEntity));
        when(orderRepository.save(orderEntity)).thenReturn(orderEntity);

        orderService.orderToNextStatus(orderId, staffId);

        assertEquals(Status.PREPARATION, orderEntity.getStatus());
        verify(orderRepository, times(1)).findById(orderId);
        verify(staffRepository, times(1)).findById(staffId);
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
        orderEntity.setStatus(Status.ACCEPTED);

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
