package ru.egartech.staff.service.mapper;

import org.mapstruct.*;
import ru.egartech.staff.entity.ManualEntity;
import ru.egartech.staff.entity.ManualId;
import ru.egartech.staff.entity.MaterialEntity;
import ru.egartech.staff.entity.ProductEntity;
import ru.egartech.staff.model.ManualDto;

import java.util.List;

@Mapper(componentModel="spring")
public interface ManualMapper {
    List<ManualDto> toManualDtoList(List<ManualEntity> manual);

    @Mapping(target = "materialId", source = "id.materialId")
    ManualDto toManualDto(ManualEntity manual);

    @Mapping(target = "id", expression = "java(createManualId(product.getId(), material.getId()))")
    ManualEntity toManualEntity(ManualDto manualDto, ProductEntity product, MaterialEntity material);

    default ManualId createManualId(Long productId, Long materialId){
        ManualId manualId = new ManualId();
        manualId.setProductId(productId);
        manualId.setMaterialId(materialId);
        return manualId;
    }
}
