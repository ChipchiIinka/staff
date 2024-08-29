package ru.egartech.staff.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.egartech.staff.entity.OrderEntity;
import ru.egartech.staff.entity.StaffEntity;
import ru.egartech.staff.entity.enums.Status;
import ru.egartech.staff.exception.ErrorType;
import ru.egartech.staff.exception.StaffException;
import ru.egartech.staff.model.*;
import ru.egartech.staff.repository.OrderRepository;
import ru.egartech.staff.repository.StaffRepository;
import ru.egartech.staff.service.mapper.OrderMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ProductService productService;
    private final StaffRepository staffRepository;

    @Transactional
    public OrderMaterialInfoResponseDto getAllNeededMaterialsInfo(Long orderId) {
        List<Long> productList = orderRepository.findOrderProducts(orderId);
        List<ManualInfoResponseDto> manuals = new ArrayList<>();
        //Создаем список со всеми нужными материалами и их количеством
        productList.stream()
                .map(productId -> orderMapper.toManualInfoDto(productService.getManualMapInfo(productId)))
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

    public OrderInfoPagingResponseDto getAllOrders(Integer pageNo, Integer pageSize,
                                                   String sortType, String sortFieldName){
        Sort sort = Sort.by(Sort.Direction.fromString(sortType), sortFieldName);
        PagingDto paging = new PagingDto();
        Page<OrderEntity> orderEntities = orderRepository.findAll(
                PageRequest.of(pageNo, pageSize, sort));
        paging.setPageNumber(pageNo);
        paging.setPageSize(pageSize);
        paging.setCount(orderEntities.getTotalElements());
        paging.setPages(orderEntities.getTotalPages());
        return new OrderInfoPagingResponseDto()
                .paging(paging)
                .content(orderMapper.toListDto(orderEntities));
    }

    @Transactional
    public OrderInfoResponseDto getOrderById(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new StaffException(ErrorType.NOT_FOUND, "Заказ не найден"));
        return orderMapper.toInfoResponseDto(order);
    }

    @Transactional
    public void createOrder(OrderSaveRequestDto orderDto) {
        OrderEntity order = new OrderEntity();
        StaffEntity manager = staffRepository.findById(orderDto.getManagerId())
                .orElseThrow(() -> new StaffException(ErrorType.NOT_FOUND, "Сотрудник не найден"));
        orderRepository.save(orderMapper.toEntity(manager, orderDto, order));
        Long orderId = order.getId();
        List<Long> productList = orderDto.getOrderProducts();
        productList.forEach(productId -> orderRepository.addProductToOrderProducts(productId, orderId));
    }

    public void orderToNextStatus(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new StaffException(ErrorType.NOT_FOUND, "Заказ не найден"));
        toNextStatus(order);
        orderRepository.save(order);
    }

    public void orderToPreparationStatus(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new StaffException(ErrorType.NOT_FOUND, "Заказ с таким id не найден"));
        if (order.getStatus().equals(Status.ASSEMBLY) ||
                order.getStatus().equals(Status.PACKAGING) ||
                order.getStatus().equals(Status.DELIVERY)) {
            order.setStatus(Status.PREPARATION);
            orderRepository.save(order);
        } else throw new StaffException(ErrorType.CLIENT_ERROR, "Нельзя перезапустить заказ с этого состояния");
    }

    public void deleteOrderById(Long orderId) {
        orderRepository.deleteById(orderId);
    }

    private void toNextStatus(OrderEntity order){
        Status status = order.getStatus();
        switch (status){
            case ACCEPTANCE -> order.setStatus(Status.ACCEPTED);
            case ACCEPTED -> order.setStatus(Status.PREPARATION);
            case PREPARATION -> order.setStatus(Status.ASSEMBLY);
            case ASSEMBLY -> order.setStatus(Status.PACKAGING);
            case PACKAGING -> order.setStatus(Status.DELIVERY);
            case DELIVERY -> order.setStatus(Status.COMPLETED);
            default -> order.setStatus(Status.CANCELED);
        }
    }
}
