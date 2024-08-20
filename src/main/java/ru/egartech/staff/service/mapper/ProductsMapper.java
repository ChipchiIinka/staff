package ru.egartech.staff.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.egartech.staff.entity.ProductEntity;
import ru.egartech.staff.model.ManualSaveRequestDto;
import ru.egartech.staff.model.ProductInfoResponseDto;
import ru.egartech.staff.model.ProductListInfoResponseDto;
import ru.egartech.staff.model.ProductSaveRequestDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ProductsMapper {

    List<ProductListInfoResponseDto> toListDto(List<ProductEntity> products);

    //Только для List<ProductListInfoResponseDto>
    @Mapping(source = "price", target = "price", numberFormat = "#0.00")
    ProductListInfoResponseDto toDto(ProductEntity product);

    default ProductInfoResponseDto toDto(ProductEntity product, List<ManualSaveRequestDto> manual){
        return new ProductInfoResponseDto().id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .type(product.getType())
                .price(product.getPrice().doubleValue())
                .manual(manual);
    }

    default ProductEntity toEntity(ProductSaveRequestDto productDto, ProductEntity productEntity){
        productEntity.setName(productDto.getName());
        productEntity.setDescription(productDto.getDescription());
        productEntity.setType(productDto.getType());
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