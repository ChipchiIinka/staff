package ru.egartech.staff.service.mapper;

import org.mapstruct.*;
import org.springframework.data.domain.Page;
import ru.egartech.staff.entity.OrderEntity;
import ru.egartech.staff.entity.ProductEntity;
import ru.egartech.staff.entity.StaffEntity;
import ru.egartech.staff.entity.enums.Position;
import ru.egartech.staff.entity.enums.Status;
import ru.egartech.staff.model.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper(componentModel="spring")
public interface OrderMapper {

    List<OrderListInfoResponseDto> toListDto(Page<OrderEntity> orders);

    //только для List<OrderListInfoResponseDto>
    @Mapping(target = "status", expression = "java(mapStatus(order.getOrderDetails().getStatus()))")
    @Mapping(target = "isPaid", source = "orderDetails.paid")
    @Mapping(target = "amount", expression = "java(order.getAmount().doubleValue())")
    OrderListInfoResponseDto toDto(OrderEntity order);

    @Mapping(target = "status", expression = "java(mapStatus(order.getOrderDetails().getStatus()))")
    @Mapping(target = "description", source = "orderDetails.description")
    @Mapping(target = "isPaid", source = "orderDetails.paid")
    @Mapping(target = "amount", expression = "java(order.getAmount().doubleValue())")
    @Mapping(target = "orderProducts", ignore = true)
    @Mapping(target = "orderStaff", ignore = true)
    OrderInfoResponseDto toInfoResponseDto(OrderEntity order);

    @Mapping(target = "address", expression = "java(toFullAddress(orderDto))")
    @Mapping(target = "date", expression = "java(java.time.LocalDate.now())")
    @Mapping(target = "orderDetails.status", expression = "java(ru.egartech.staff.entity.enums.Status.ACCEPTED)")
    @Mapping(target = "orderDetails.staff", source = "staff")
    @Mapping(target = "orderDetails.products", source = "products")
    @Mapping(target = "orderDetails.description", source = "orderDto.description")
    @Mapping(target = "id", ignore = true)
    OrderEntity toEntity(OrderSaveRequestDto orderDto, @MappingTarget OrderEntity orderEntity, List<StaffEntity> staff,
                         List<ProductEntity> products, BigDecimal amount);

    default String toFullAddress(OrderSaveRequestDto orderDto) {
        return String.format("%s, %s, %s", orderDto.getCity(), orderDto.getStreet(), orderDto.getHouse());
    }

    default List<StaffShortInfoDto> toStaffShortInfo(List<StaffEntity> staffEntity) {
        return staffEntity.stream()
                .map(staff -> new StaffShortInfoDto()
                        .id(staff.getId())
                        .fullName(staff.getFullName())
                        .position(Position.toPositionDto(staff.getPosition())))
                .toList();
    }

    default List<ProductShortInfoDto> toProductShortInfo(Map<ProductEntity, Long> products) {
        return products.entrySet().stream()
                .map(entry -> new ProductShortInfoDto()
                        .id(entry.getKey().getId())
                        .name(entry.getKey().getName())
                        .price(entry.getKey().getPrice().doubleValue())
                        .quantity(Math.toIntExact(entry.getValue())))
                .toList();
    }

    default OrderStatusDto mapStatus(Status status) {
        return Status.toOrderDtoStatus(status);
    }
}
