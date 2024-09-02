package ru.egartech.staff.service.mapper;

import org.mapstruct.*;
import org.springframework.data.domain.Page;
import ru.egartech.staff.entity.OrderEntity;
import ru.egartech.staff.entity.ProductEntity;
import ru.egartech.staff.entity.StaffEntity;
import ru.egartech.staff.entity.projection.ManualProjection;
import ru.egartech.staff.model.ManualDto;
import ru.egartech.staff.model.OrderInfoResponseDto;
import ru.egartech.staff.model.OrderListInfoResponseDto;
import ru.egartech.staff.model.OrderSaveRequestDto;

import java.util.List;

@Mapper(componentModel="spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {
    String DEFAULT_STAFF_ENUMS_PACKAGE = "ru.egartech.staff.entity.enums.";

    List<OrderListInfoResponseDto> toListDto(Page<OrderEntity> orders);

    //только для List<OrderListInfoResponseDto>
    @Mapping(target = "status", expression = "java(" + DEFAULT_STAFF_ENUMS_PACKAGE +
            "Status.toOrderDtoStatus(order.getStatus()))")
    OrderListInfoResponseDto toDto(OrderEntity order);

    @Mapping(target = "status", expression = "java(" + DEFAULT_STAFF_ENUMS_PACKAGE +
            "Status.toOrderDtoStatus(order.getStatus()))")
    @Mapping(target = "orderProducts", expression = "java(order.getProducts().stream().map(ProductEntity::getId).toList())")
    @Mapping(target = "orderStaff", expression = "java(order.getStaff().stream().map(StaffEntity::getId).toList())")
    OrderInfoResponseDto toInfoResponseDto(OrderEntity order);

    @Mapping(target = "address", expression = "java(toFullAddress(orderDto))")
    @Mapping(target = "date", expression = "java(java.time.LocalDate.now())")
    @Mapping(target = "status", expression = "java(" + DEFAULT_STAFF_ENUMS_PACKAGE +
            "Status.ACCEPTED)")
    OrderEntity toEntity(List<StaffEntity> staff, OrderSaveRequestDto orderDto,
                                 @MappingTarget OrderEntity orderEntity, List<ProductEntity> products);

    default List<ManualDto> toManualDto(List<ManualProjection> manualProjection){
        return manualProjection.stream()
                .map(projection -> new ManualDto()
                        .material(projection.getMaterial())
                        .quantity(projection.getQuantity()))
                .toList();
    }

    default String toFullAddress(OrderSaveRequestDto orderDto) {
        return String.format("%s, %s, %s", orderDto.getCity(), orderDto.getStreet(), orderDto.getHouse());
    }
}
