package ru.egartech.staff.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.egartech.staff.entity.MaterialEntity;
import ru.egartech.staff.model.MaterialInfoResponseDto;
import ru.egartech.staff.model.MaterialListInfoResponseDto;
import ru.egartech.staff.model.MaterialSaveRequestDto;

import java.util.List;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface MaterialsMapper {

    List<MaterialListInfoResponseDto> toListDto(List<MaterialEntity> materials);

    default MaterialListInfoResponseDto toDto(MaterialEntity material){
        return new MaterialListInfoResponseDto()
                .name(material.getName())
                .type(material.getType())
                .shortInfo(String.format("%d / %d / %d",
                        material.getLength(), material.getWidth(), material.getHeight()));
    }

    MaterialInfoResponseDto toDto(MaterialEntity material, Long quantity);

    default MaterialEntity toEntity(MaterialSaveRequestDto materialDto, MaterialEntity material){
        material.setName(materialDto.getName());
        material.setDescription(materialDto.getDescription());
        material.setType(materialDto.getType());
        material.setLength(materialDto.getLength());
        material.setWidth(materialDto.getWidth());
        material.setHeight(materialDto.getHeight());
        return material;
    }
}
