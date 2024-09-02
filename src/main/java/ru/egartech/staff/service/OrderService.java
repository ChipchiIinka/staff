package ru.egartech.staff.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.egartech.staff.entity.OrderEntity;
import ru.egartech.staff.entity.ProductEntity;
import ru.egartech.staff.entity.StaffEntity;
import ru.egartech.staff.entity.enums.Status;
import ru.egartech.staff.entity.projection.ManualProjection;
import ru.egartech.staff.exception.ErrorType;
import ru.egartech.staff.exception.StaffException;
import ru.egartech.staff.model.*;
import ru.egartech.staff.repository.OrderRepository;
import ru.egartech.staff.repository.ProductRepository;
import ru.egartech.staff.repository.StaffRepository;
import ru.egartech.staff.service.mapper.OrderMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final StaffRepository staffRepository;
    private final ProductRepository productRepository;

    @Transactional
    public OrderMaterialInfoResponseDto getAllNeededMaterialsInfo(Long orderId) {
        List<ProductEntity> productList = orderRepository.findOrderProducts(orderId);
        //Нахождение количества материалов с одинаковым id путем сложения
        Map<Long, ManualDto> materialMap = productList.stream()
                .flatMap(product -> productRepository.findProductManualProjection(product.getId()).stream())
                .collect(Collectors.toMap(
                        ManualProjection::getMaterial,
                        projection -> new ManualDto().material(projection.getMaterial()).quantity(projection.getQuantity()),
                        (existing, incoming) -> {
                            existing.setQuantity(existing.getQuantity() + incoming.getQuantity());
                            return existing;
                        }
                ));
        OrderMaterialInfoResponseDto orderMaterialInfoResponseDto = new OrderMaterialInfoResponseDto();
        orderMaterialInfoResponseDto.setId(orderId);
        orderMaterialInfoResponseDto.setNeededMaterials(new ArrayList<>(materialMap.values()));
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
        List<StaffEntity> staff = List.of(staffRepository.findById(orderDto.getManagerId())
                .orElseThrow(() -> new StaffException(ErrorType.NOT_FOUND, "Сотрудник не найден")));

        List<ProductEntity> productList = orderDto.getOrderProducts().stream()
                .map(productId -> productRepository.findById(productId)
                        .orElseThrow(() -> new StaffException(ErrorType.NOT_FOUND, "Товар не найден с id=" + productId)))
                .toList();
        orderRepository.save(orderMapper.toEntity(staff, orderDto, order, productList));
    }

    @Transactional
    public void orderToNextStatus(Long orderId, Long staffId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new StaffException(ErrorType.NOT_FOUND, "Заказ не найден"));
        StaffEntity staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new StaffException(ErrorType.NOT_FOUND, "Сотрудник не найден"));
        if (!order.getStaff().contains(staff)){
            order.getStaff().add(staff);
        }
        toNextStatus(order);
        orderRepository.save(order);
    }

    @Transactional
    public void orderToPreparationStatus(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new StaffException(ErrorType.NOT_FOUND, "Заказ с таким id не найден"));
        if (Status.canRestart.contains(order.getStatus())) {
            order.setStatus(Status.PREPARATION);
            orderRepository.save(order);
        } else throw new StaffException(ErrorType.CLIENT_ERROR, "Нельзя перезапустить заказ с этого состояния");
    }

    @Transactional
    public void deleteOrderById(Long orderId) {
        orderRepository.deleteById(orderId);
    }

    private void toNextStatus(OrderEntity order){
        Status status = order.getStatus();
        switch (status){
            case ACCEPTED -> order.setStatus(Status.PREPARATION);
            case PREPARATION -> order.setStatus(Status.ASSEMBLY);
            case ASSEMBLY -> order.setStatus(Status.PACKAGING);
            case PACKAGING -> order.setStatus(Status.WAITING_FOR_DELIVERY);
            case WAITING_FOR_DELIVERY -> order.setStatus(Status.DELIVERY);
            case DELIVERY -> order.setStatus(Status.COMPLETED);
            default -> order.setStatus(Status.CANCELED);
        }
    }
}
