package ru.egartech.staff.service.mapper;

import org.mapstruct.*;
import org.springframework.data.domain.Page;
import ru.egartech.staff.entity.MaterialEntity;
import ru.egartech.staff.model.MaterialInfoResponseDto;
import ru.egartech.staff.model.MaterialListInfoResponseDto;
import ru.egartech.staff.model.MaterialSaveRequestDto;

import java.util.List;

@Mapper(componentModel="spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MaterialMapper {
    String DEFAULT_STAFF_ENUMS_PACKAGE = "ru.egartech.staff.entity.enums.";

    List<MaterialListInfoResponseDto> toListDto(Page<MaterialEntity> materials);

    @Mapping(target = "shortInfo", expression = "java(buildShortInfo(material))")
    @Mapping(target = "type", expression = "java(" + DEFAULT_STAFF_ENUMS_PACKAGE +
            "MaterialType.toMaterialDtoType(material.getType()))")
    MaterialListInfoResponseDto toDto(MaterialEntity material);

    @Mapping(target = "type", expression = "java(" + DEFAULT_STAFF_ENUMS_PACKAGE +
            "MaterialType.toMaterialDtoType(material.getType()))")
    MaterialInfoResponseDto toDto(MaterialEntity material, Long available);

    @Mapping(target = "type", expression = "java(" + DEFAULT_STAFF_ENUMS_PACKAGE +
            "MaterialType.toMaterialEntityType(materialDto.getType()))")
    MaterialEntity toEntity(MaterialSaveRequestDto materialDto, @MappingTarget MaterialEntity material);

    default String buildShortInfo(MaterialEntity material){
        return String.format("%d / %d / %d", material.getLength(), material.getWidth(), material.getHeight());
    }
}
