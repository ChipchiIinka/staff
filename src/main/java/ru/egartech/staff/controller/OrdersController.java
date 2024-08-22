package ru.egartech.staff.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.egartech.staff.api.OrdersApi;
import ru.egartech.staff.model.OrderInfoResponseDto;
import ru.egartech.staff.model.OrderListInfoResponseDto;
import ru.egartech.staff.model.OrderMaterialInfoResponseDto;
import ru.egartech.staff.model.OrderSaveRequestDto;
import ru.egartech.staff.service.OrdersService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrdersController implements OrdersApi {

    private final OrdersService ordersService;

    @Override
    public OrderMaterialInfoResponseDto getAllNeededMaterials(Long orderId) {
        return ordersService.getAllNeededMaterialsInfo(orderId);
    }

    @Override
    public List<OrderListInfoResponseDto> getAllOrders() {
        return ordersService.getAllOrders();
    }

    @Override
    public OrderInfoResponseDto getOrderById(Long orderId) {
        return ordersService.getOrderById(orderId);
    }

    @Override
    public void createOrder(OrderSaveRequestDto orderSaveRequestDto) {
        ordersService.createOrder(orderSaveRequestDto);
    }

    @Override
    public void updateOrderStatus(Long orderId) {
        ordersService.orderToNextStatus(orderId);
    }

    @Override
    public void updateOrderStatusToPreparation(Long orderId) {
        ordersService.orderToPreparationStatus(orderId);
    }
}
