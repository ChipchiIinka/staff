package ru.egartech.staff.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.egartech.staff.api.OrdersApi;
import ru.egartech.staff.model.OrderInfoPagingResponseDto;
import ru.egartech.staff.model.OrderInfoResponseDto;
import ru.egartech.staff.model.OrderMaterialInfoResponseDto;
import ru.egartech.staff.model.OrderSaveRequestDto;
import ru.egartech.staff.service.OrderService;

@RestController
@RequiredArgsConstructor
public class OrderRestController implements OrdersApi {

    private final OrderService orderService;

    @Override
    public OrderMaterialInfoResponseDto getAllNeededMaterials(Long orderId) {
        return orderService.getAllNeededMaterialsInfo(orderId);
    }

    @Override
    public OrderInfoPagingResponseDto getAllOrders(Integer pageNumber, Integer pageSize,
                                                   String sortType, String sortFieldName, String searchingFilter) {
        return orderService.getAllOrders(pageNumber, pageSize, sortType, sortFieldName, searchingFilter);
    }

    @Override
    public OrderInfoResponseDto getOrderById(Long orderId) {
        return orderService.getOrderById(orderId);
    }

    @Override
    public void createOrder(OrderSaveRequestDto orderSaveRequestDto) {
        orderService.createOrder(orderSaveRequestDto);
    }

    @Override
    public void updateOrderStatus(Long orderId, Long staffId) {
        orderService.orderToNextStatus(orderId, staffId);
    }

    @Override
    public void updateOrderStatusToPreparation(Long orderId) {
        orderService.orderToPreparationStatus(orderId);
    }

    @Override
    public void updateOrderStatusToCancel(Long orderId) {
        orderService.orderToCancelStatus(orderId);
    }

    @Override
    public void deleteOrderById(Long orderId) {
        orderService.deleteOrderById(orderId);
    }
}
