package ru.egartech.staff.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.egartech.staff.entity.OrderEntity;
import ru.egartech.staff.entity.enums.Status;
import ru.egartech.staff.model.*;
import ru.egartech.staff.repository.OrdersRepository;
import ru.egartech.staff.service.mapper.OrdersMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.egartech.staff.entity.enums.Status.toNextStatus;

@Service
@RequiredArgsConstructor
public class OrdersService {
    private final OrdersRepository ordersRepository;
    private final OrdersMapper ordersMapper;
    private final ProductsService productsService;

    @Transactional
    public OrderMaterialInfoResponseDto getAllNeededMaterialsInfo(Long orderId) {
        List<Long> productList = ordersRepository.findOrderProducts(orderId);
        List<ManualInfoResponseDto> manuals = new ArrayList<>();
        //Создаем список со всеми нужными материалами и их количеством
        productList.stream()
                .map(productId -> ordersMapper.toManualInfoDto(productsService.getManualMapInfo(productId)))
                .forEach(manuals::addAll);
        //Повторяющиеся материалы объединяются в один, а их количество складывается
        List<ManualInfoResponseDto> manualsResponse = manuals.stream()
                .collect(Collectors.toMap(
                        ManualInfoResponseDto::getId,
                        manual -> manual,
                        (manual1, manual2) -> {
                            manual1.setQuantity(manual1.getQuantity() + manual2.getQuantity());
                            return manual1;
                        }))
                .values()
                .stream()
                .toList();
        OrderMaterialInfoResponseDto orderMaterialInfoResponseDto = new OrderMaterialInfoResponseDto();
        orderMaterialInfoResponseDto.setId(orderId);
        orderMaterialInfoResponseDto.setNeededMaterials(manualsResponse);
        return orderMaterialInfoResponseDto;
    }

    public List<OrderListInfoResponseDto> getAllOrders(){
        return ordersMapper.toListDto(ordersRepository.findAll());
    }

    @Transactional
    public OrderInfoResponseDto getOrderById(Long orderId) {
        //Если не сработает order.getProducts - в маппере
        /* List<Long> orderProductsId = ordersRepository.findOrderProducts(orderId);
        List<String> orderProductsNames = orderProductsId.stream()
                .map(productId -> productsRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("Product not found"))
                        .getName())
                .toList(); */

        OrderEntity order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found")); //TODO временная заглушка, поменять на обработчик
        return ordersMapper.toInfoResponseDto(order);
    }

    @Transactional
    public void createOrder(OrderSaveRequestDto orderDto) {
        OrderEntity order = new OrderEntity();
        ordersRepository.save(ordersMapper.toEntity(orderDto, order));
        Long orderId = order.getId();
        List<Long> productList = orderDto.getOrderProducts();
        productList.forEach(productId -> ordersRepository.addProductToOrderProducts(productId, orderId));
    }

    public void orderToNextStatus(Long orderId) {
        OrderEntity order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found")); //TODO временная заглушка, поменять на обработчик
        toNextStatus(order);
        ordersRepository.save(order);
    }

    public void orderToPreparationStatus(Long orderId) {
        OrderEntity order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found")); //TODO временная заглушка, поменять на обработчик
        order.setStatus(Status.PREPARATION);
        ordersRepository.save(order);
    }
}
