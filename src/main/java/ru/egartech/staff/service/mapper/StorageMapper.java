package ru.egartech.staff.service.mapper;

import org.mapstruct.*;
import org.springframework.data.domain.Page;
import ru.egartech.staff.entity.StorageEntity;
import ru.egartech.staff.entity.projection.MaterialStorageProjection;
import ru.egartech.staff.entity.projection.ProductStorageProjection;
import ru.egartech.staff.model.*;

import java.util.List;

@Mapper(componentModel="spring")
public interface StorageMapper {

    List<StorageListInfoResponseDto> toListDto(Page<StorageEntity> storageEntities);

    StorageInfoResponseDto toDto(StorageEntity storageEntity,
                                 List<ProductStorageInfoDto> products,
                                 List<MaterialStorageInfoDto> materials);

    @Mapping(target = "address", expression = "java(toFullAddress(saveRequestDto))")
    @Mapping(target = "id", ignore = true)
    StorageEntity toEntity(StorageSaveRequestDto saveRequestDto, @MappingTarget StorageEntity storageEntity);

    default List<ProductStorageInfoDto> toProductStorageListDto(List<ProductStorageProjection> productStorage) {
        return productStorage.stream()
                .map(ps -> new ProductStorageInfoDto()
                        .id(ps.getProductId())
                        .quantity(ps.getAvailable()))
                .toList();
    }

    default List<MaterialStorageInfoDto> toMaterialStorageListDto(List<MaterialStorageProjection> materialStorage) {
        return materialStorage.stream()
                .map(ms -> new MaterialStorageInfoDto()
                        .id(ms.getMaterialId())
                        .quantity(ms.getAvailable()))
                .toList();
    }

     default String toFullAddress(StorageSaveRequestDto dto) {
        return String.format("%s, %s, %s", dto.getCity(), dto.getStreet(), dto.getHouse());
    }
}
