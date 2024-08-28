package ru.egartech.staff.service.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.egartech.staff.entity.MaterialEntity;
import ru.egartech.staff.entity.enums.MaterialType;
import ru.egartech.staff.model.MaterialInfoResponseDto;
import ru.egartech.staff.model.MaterialListInfoResponseDto;
import ru.egartech.staff.model.MaterialSaveRequestDto;
import ru.egartech.staff.model.MaterialTypeDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = MaterialMapperImpl.class)
class MaterialMapperTest {

    @Autowired
    private MaterialMapper materialMapper;

    @Test
    void testToListDto() {
        MaterialEntity material1 = createMaterial(1L);
        MaterialEntity material2 = createMaterial(2L);

        List<MaterialEntity> materialList = List.of(material1, material2);
        Page<MaterialEntity> materialPage = new PageImpl<>(materialList, PageRequest.of(0, 10), materialList.size());

        List<MaterialListInfoResponseDto> result = materialMapper.toListDto(materialPage);

        assertEquals(2, result.size());
        assertEquals("Material 1", result.get(0).getName());
        assertEquals("Material 2", result.get(1).getName());
        assertEquals("10 / 20 / 30", result.get(0).getShortInfo());
        assertEquals("20 / 40 / 60", result.get(1).getShortInfo());
    }

    @Test
    void testToDto() {
        MaterialEntity material = createMaterial(1L);

        MaterialListInfoResponseDto dto = materialMapper.toDto(material);

        assertNotNull(dto);
        assertEquals("Material 1", dto.getName());
        assertEquals("10 / 20 / 30", dto.getShortInfo());
    }

    @Test
    void testToDtoWithAvailable() {
        MaterialEntity material = createMaterial(1L);

        Long available = 100L;
        MaterialInfoResponseDto dto = materialMapper.toDto(material, available);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Material 1", dto.getName());
        assertEquals("Description 1", dto.getDescription());
        assertEquals(MaterialTypeDto.WOOD, dto.getType());
        assertEquals(10, dto.getLength());
        assertEquals(20, dto.getWidth());
        assertEquals(30, dto.getHeight());
        assertEquals(available, dto.getAvailable());
    }

    @Test
    void testToEntity() {
        MaterialSaveRequestDto dto = new MaterialSaveRequestDto()
                .name("Material 1")
                .description("Description 1")
                .type(MaterialTypeDto.WOOD)
                .length(10)
                .width(20)
                .height(30);

        MaterialEntity materialEntity = new MaterialEntity();
        materialEntity = materialMapper.toEntity(dto, materialEntity);

        assertNotNull(materialEntity);
        assertEquals("Material 1", materialEntity.getName());
        assertEquals("Description 1", materialEntity.getDescription());
        assertEquals(MaterialType.WOOD, materialEntity.getType());
        assertEquals(10, materialEntity.getLength());
        assertEquals(20, materialEntity.getWidth());
        assertEquals(30, materialEntity.getHeight());
    }

    private static MaterialEntity createMaterial(Long id){
        MaterialEntity material = new MaterialEntity();
        material.setId(id);
        material.setName("Material " + id);
        material.setDescription("Description " + id);
        material.setType(MaterialType.WOOD);
        material.setLength(10 * id.intValue());
        material.setWidth(20 * id.intValue());
        material.setHeight(30 * id.intValue());
        return material;
    }
}