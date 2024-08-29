package ru.egartech.staff.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.Page;
import ru.egartech.staff.entity.ProductEntity;
import ru.egartech.staff.entity.enums.ProductType;
import ru.egartech.staff.model.ManualSaveRequestDto;
import ru.egartech.staff.model.ProductInfoResponseDto;
import ru.egartech.staff.model.ProductListInfoResponseDto;
import ru.egartech.staff.model.ProductSaveRequestDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper(componentModel="spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ProductMapper {

    List<ProductListInfoResponseDto> toListDto(Page<ProductEntity> products);

    //Только для List<ProductListInfoResponseDto>
    default ProductListInfoResponseDto toDto(ProductEntity product){
        return new ProductListInfoResponseDto()
                .name(product.getName())
                .type(ProductType.toProductDtoType(product.getType()))
                .price(product.getPrice().doubleValue());
    }

    default ProductInfoResponseDto toDto(ProductEntity product, List<ManualSaveRequestDto> manual, Long available){
        return new ProductInfoResponseDto()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .type(ProductType.toProductDtoType(product.getType()))
                .price(product.getPrice().doubleValue())
                .manual(manual)
                .available(available);
    }

    default ProductEntity toEntity(ProductSaveRequestDto productDto, ProductEntity productEntity){
        productEntity.setName(productDto.getName());
        productEntity.setDescription(productDto.getDescription());
        productEntity.setType(ProductType.toProductEntityType(productDto.getType()));
        productEntity.setPrice(BigDecimal.valueOf(productDto.getPrice()));
        return productEntity;
    }

    default List<ManualSaveRequestDto> toManualSaveRequestDto(Map<Long, Integer> manualFromDB) {
        return manualFromDB.entrySet()
                .stream()
                .map(entry -> {
                    ManualSaveRequestDto dto = new ManualSaveRequestDto();
                    dto.setMaterial(entry.getKey());
                    dto.setQuantity(entry.getValue());
                    return dto;
                })
                .toList();
    }
}