package ru.egartech.staff.service.mapper;

import org.mapstruct.*;
import org.springframework.data.domain.Page;
import ru.egartech.staff.entity.ProductEntity;
import ru.egartech.staff.entity.enums.ProductType;
import ru.egartech.staff.model.*;

import java.util.List;

@Mapper(componentModel="spring")
public interface ProductMapper {

    List<ProductListInfoResponseDto> toListDto(Page<ProductEntity> products);

    //Только для List<ProductListInfoResponseDto>
    @Mapping(target = "type", expression = "java(mapType(product.getType()))")
    @Mapping(target = "price", expression = "java(product.getPrice().doubleValue())")
    ProductListInfoResponseDto toDto(ProductEntity product);

    @Mapping(target = "type", expression = "java(mapType(product.getType()))")
    @Mapping(target = "price", expression = "java(product.getPrice().doubleValue())")
    ProductInfoResponseDto toDto(ProductEntity product, List<ManualDto> manual, Long available);

    @Mapping(target = "type", expression = "java(mapType(productDto.getType()))")
    @Mapping(target = "price", expression = "java(java.math.BigDecimal.valueOf(productDto.getPrice()))")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "manuals", ignore = true)
    ProductEntity toEntity(ProductSaveRequestDto productDto, @MappingTarget ProductEntity productEntity);

    default ProductTypeDto mapType(ProductType type) {
        return ProductType.toProductDtoType(type);
    }

    default ProductType mapType(ProductTypeDto type) {
        return ProductType.toProductEntityType(type);
    }
}