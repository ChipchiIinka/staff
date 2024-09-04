package ru.egartech.staff.service.mapper;

import org.mapstruct.*;
import org.springframework.data.domain.Page;
import ru.egartech.staff.entity.MaterialEntity;
import ru.egartech.staff.entity.enums.MaterialType;
import ru.egartech.staff.model.MaterialInfoResponseDto;
import ru.egartech.staff.model.MaterialListInfoResponseDto;
import ru.egartech.staff.model.MaterialSaveRequestDto;
import ru.egartech.staff.model.MaterialTypeDto;

import java.util.List;

@Mapper(componentModel="spring")
public interface MaterialMapper {

    List<MaterialListInfoResponseDto> toListDto(Page<MaterialEntity> materials);

    @Mapping(target = "shortInfo", expression = "java(buildShortInfo(material))")
    @Mapping(target = "type", expression = "java(mapType(material.getType()))")
    MaterialListInfoResponseDto toDto(MaterialEntity material);

    @Mapping(target = "type", expression = "java(mapType(material.getType()))")
    MaterialInfoResponseDto toDto(MaterialEntity material, Long available);

    @Mapping(target = "type", expression = "java(mapType(materialDto.getType()))")
    @Mapping(target = "id", ignore = true)
    MaterialEntity toEntity(MaterialSaveRequestDto materialDto, @MappingTarget MaterialEntity material);

    default String buildShortInfo(MaterialEntity material){
        return String.format("%d / %d / %d", material.getLength(), material.getWidth(), material.getHeight());
    }

    default MaterialTypeDto mapType(MaterialType type) {
        return MaterialType.toMaterialDtoType(type);
    }

    default MaterialType mapType(MaterialTypeDto type) {
        return MaterialType.toMaterialEntityType(type);
    }
}
