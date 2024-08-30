package ru.egartech.staff.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.Page;
import ru.egartech.staff.entity.OrderEntity;
import ru.egartech.staff.entity.ProductEntity;
import ru.egartech.staff.entity.StaffEntity;
import ru.egartech.staff.entity.enums.Status;
import ru.egartech.staff.model.ManualInfoResponseDto;
import ru.egartech.staff.model.OrderInfoResponseDto;
import ru.egartech.staff.model.OrderListInfoResponseDto;
import ru.egartech.staff.model.OrderSaveRequestDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper(componentModel="spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface OrderMapper {

    List<OrderListInfoResponseDto> toListDto(Page<OrderEntity> orders);

    //только для List<OrderListInfoResponseDto>
    default OrderListInfoResponseDto toDto(OrderEntity order){
        return new OrderListInfoResponseDto()
                .address(order.getAddress())
                .date(order.getDate())
                .status(Status.toOrderDtoStatus(order.getStatus()));
    }

    default OrderInfoResponseDto toInfoResponseDto(OrderEntity order){
        return new OrderInfoResponseDto()
                .id(order.getId())
                .address(order.getAddress())
                .date(order.getDate())
                .status(Status.toOrderDtoStatus(order.getStatus()))
                .orderProducts(order.getProducts().stream().map(ProductEntity::getId).toList())
                .orderStaff(order.getStaff().stream().map(StaffEntity::getId).toList());

    }

    default OrderEntity toEntity(StaffEntity manager, OrderSaveRequestDto orderDto, OrderEntity orderEntity) {
        orderEntity.setStaff(List.of(manager));
        orderEntity.setAddress(String.format("%s, %s, %s", orderDto.getCity(), orderDto.getStreet(), orderDto.getHouse()));
        orderEntity.setDate(LocalDate.now());
        orderEntity.setStatus(Status.ACCEPTED);
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
}
