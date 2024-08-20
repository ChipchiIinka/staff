package ru.egartech.staff.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.egartech.staff.entity.OrderEntity;
import ru.egartech.staff.entity.ProductEntity;
import ru.egartech.staff.entity.enums.Status;
import ru.egartech.staff.model.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface OrdersMapper {

    List<OrderListInfoResponseDto> toListDto(List<OrderEntity> orders);

    //только для List<OrderListInfoResponseDto>
    default OrderListInfoResponseDto toDto(OrderEntity order){
        return new OrderListInfoResponseDto()
                .address(order.getAddress())
                .date(order.getDate())
                .status(toDtoStatus(order.getStatus()));
    }

    default OrderInfoResponseDto toInfoResponseDto(OrderEntity order){
        return new OrderInfoResponseDto()
                .id(order.getId())
                .address(order.getAddress())
                .date(order.getDate())
                .status(toDtoStatus(order.getStatus()))
                .orderProducts(order.getProducts().stream().map(ProductEntity::getName).toList());

    }

    default OrderEntity toEntity(OrderSaveRequestDto orderDto, OrderEntity orderEntity) {
        orderEntity.setAddress(String.format("%s, %s, %s", orderDto.getCity(), orderDto.getStreet(), orderDto.getHouse()));
        orderEntity.setDate(LocalDate.now());
        orderEntity.setStatus(Status.ACCEPTANCE);
        return orderEntity;
    }

    default List<ManualInfoResponseDto> toManualInfoDto(Map<Long, Integer> manualInfoMap){
        return manualInfoMap.entrySet().stream()
                .map(manual -> {
                    ManualInfoResponseDto dto = new ManualInfoResponseDto();
                    dto.setId(manual.getKey());
                    dto.setQuantity(manual.getValue());
                    return dto;
                }).toList();
    }

    default OrderStatusDto toDtoStatus(Status status){
        return switch (status){
            case COMPLETED -> OrderStatusDto.COMPLETED;
            case DELIVERY -> OrderStatusDto.DELIVERY;
            case PACKAGING -> OrderStatusDto.PACKAGING;
            case ASSEMBLY -> OrderStatusDto.ASSEMBLY;
            case PREPARATION -> OrderStatusDto.PREPARATION;
            case ACCEPTED -> OrderStatusDto.ACCEPTED;
            case CANCELED -> OrderStatusDto.CANCELED;
            case ACCEPTANCE -> OrderStatusDto.ACCEPTANCE;
        };
    }
}
