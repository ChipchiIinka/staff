package ru.egartech.staff.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.Page;
import ru.egartech.staff.entity.MaterialEntity;
import ru.egartech.staff.entity.ProductEntity;
import ru.egartech.staff.entity.StorageEntity;
import ru.egartech.staff.model.*;

import java.util.List;
import java.util.Map;

import static ru.egartech.staff.entity.enums.MaterialType.toMaterialDtoType;
import static ru.egartech.staff.entity.enums.ProductType.toProductDtoType;

@Mapper(componentModel="spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface StorageMapper {

    List<StorageListInfoResponseDto> toListDto(Page<StorageEntity> storageEntities);

    StorageInfoResponseDto toDto(StorageEntity storageEntity,
                                 List<ProductStorageResponseDto> products,
                                 List<MaterialStorageResponseDto> materials);

    default StorageEntity toEntity(StorageSaveRequestDto saveRequestDto, StorageEntity storageEntity) {
        storageEntity.setAddress(String.format("%s, %s, %s",
                saveRequestDto.getCity(), saveRequestDto.getStreet(), saveRequestDto.getHouse()));
        return storageEntity;
    }

    default List<ProductStorageResponseDto> toProductStorageListDto(Map<ProductEntity, Integer> productEntityMap) {
        return productEntityMap.entrySet().stream()
                .map(productMap -> {
                    ProductStorageResponseDto dto = new ProductStorageResponseDto();
                    ProductEntity product = productMap.getKey();
                    dto.setId(product.getId());
                    dto.setName(product.getName());
                    dto.setType(toProductDtoType(product.getType()));
                    dto.setQuantity(productMap.getValue());
                    return dto;
                }).toList();
    }

    default List<MaterialStorageResponseDto> toMaterialStorageListDto(Map<MaterialEntity, Integer> materialEntityMap) {
        return materialEntityMap.entrySet().stream()
                .map(materialMap -> {
                    MaterialStorageResponseDto dto = new MaterialStorageResponseDto();
                    MaterialEntity material = materialMap.getKey();
                    dto.setId(material.getId());
                    dto.setName(material.getName());
                    dto.setType(toMaterialDtoType(material.getType()));
                    dto.setShortInfo(String.format("%d / %d / %d",
                            material.getLength(), material.getWidth(), material.getHeight()));
                    dto.setQuantity(materialMap.getValue());
                    return dto;
                }).toList();
    }
}
