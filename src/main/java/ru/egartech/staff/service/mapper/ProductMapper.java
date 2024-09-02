package ru.egartech.staff.service.mapper;

import org.mapstruct.*;
import org.springframework.data.domain.Page;
import ru.egartech.staff.entity.ProductEntity;
import ru.egartech.staff.entity.projection.ManualProjection;
import ru.egartech.staff.model.ManualDto;
import ru.egartech.staff.model.ProductInfoResponseDto;
import ru.egartech.staff.model.ProductListInfoResponseDto;
import ru.egartech.staff.model.ProductSaveRequestDto;

import java.util.List;

@Mapper(componentModel="spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    String DEFAULT_STAFF_ENUMS_PACKAGE = "ru.egartech.staff.entity.enums.";

    List<ProductListInfoResponseDto> toListDto(Page<ProductEntity> products);

    //Только для List<ProductListInfoResponseDto>
    @Mapping(target = "type", expression = "java(" + DEFAULT_STAFF_ENUMS_PACKAGE +
            "ProductType.toProductDtoType(product.getType()))")
    @Mapping(target = "price", expression = "java(product.getPrice().doubleValue())")
    ProductListInfoResponseDto toDto(ProductEntity product);

    @Mapping(target = "type", expression = "java(" + DEFAULT_STAFF_ENUMS_PACKAGE +
            "ProductType.toProductDtoType(product.getType()))")
    @Mapping(target = "price", expression = "java(product.getPrice().doubleValue())")
    ProductInfoResponseDto toDto(ProductEntity product, List<ManualDto> manual, Long available);

    @Mapping(target = "type", expression = "java(" + DEFAULT_STAFF_ENUMS_PACKAGE +
            "ProductType.toProductEntityType(productDto.getType()))")
    @Mapping(target = "price", expression = "java(java.math.BigDecimal.valueOf(productDto.getPrice()))")
    ProductEntity toEntity(ProductSaveRequestDto productDto, @MappingTarget ProductEntity productEntity);

    default List<ManualDto> toManualDto(List<ManualProjection> manualProjection){
        return manualProjection.stream()
                .map(projection -> new ManualDto()
                        .material(projection.getMaterial())
                        .quantity(projection.getQuantity()))
                .toList();
    }
}