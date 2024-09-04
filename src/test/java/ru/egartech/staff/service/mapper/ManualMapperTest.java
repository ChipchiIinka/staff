package ru.egartech.staff.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.egartech.staff.entity.ManualEntity;
import ru.egartech.staff.entity.ManualId;
import ru.egartech.staff.entity.MaterialEntity;
import ru.egartech.staff.entity.ProductEntity;
import ru.egartech.staff.model.ManualDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = ManualMapperImpl.class)
class ManualMapperTest {

    @Autowired
    private ManualMapper manualMapper;

    @BeforeEach
    public void setUp() {
        manualMapper = Mappers.getMapper(ManualMapper.class);
    }

    @Test
    void testToManualDto() {
        ManualId manualId = new ManualId();
        manualId.setProductId(1L);
        manualId.setMaterialId(2L);

        ManualEntity manualEntity = new ManualEntity();
        manualEntity.setId(manualId);
        manualEntity.setQuantity(10);

        ManualDto manualDto = manualMapper.toManualDto(manualEntity);

        assertNotNull(manualDto);
        assertEquals(2L, manualDto.getMaterialId());
        assertEquals(10, manualDto.getQuantity());
    }

    @Test
    void testToManualEntity() {
        Long productId = 1L;
        ManualDto manualDto = new ManualDto();
        manualDto.setQuantity(10);
        ProductEntity product = new ProductEntity();
        product.setId(productId);
        MaterialEntity material = new MaterialEntity();
        material.setId(2L);

        ManualEntity manualEntity = manualMapper.toManualEntity(manualDto, product, material);

        assertNotNull(manualEntity);
        assertEquals(productId, manualEntity.getId().getProductId());
        assertEquals(2L, manualEntity.getId().getMaterialId());
        assertEquals(10, manualEntity.getQuantity());
    }

    @Test
    void testToManualDtoList() {
        List<ManualEntity> manualEntities = getManualEntities();

        List<ManualDto> manualDtos = manualMapper.toManualDtoList(manualEntities);

        assertNotNull(manualDtos);
        assertEquals(2, manualDtos.size());

        ManualDto dto1 = manualDtos.get(0);
        assertEquals(2L, dto1.getMaterialId());
        assertEquals(10, dto1.getQuantity());

        ManualDto dto2 = manualDtos.get(1);
        assertEquals(3L, dto2.getMaterialId());
        assertEquals(20, dto2.getQuantity());
    }

    private static List<ManualEntity> getManualEntities() {
        ManualId manualId1 = new ManualId();
        manualId1.setProductId(1L);
        manualId1.setMaterialId(2L);

        ManualEntity manualEntity1 = new ManualEntity();
        manualEntity1.setId(manualId1);
        manualEntity1.setQuantity(10);

        ManualId manualId2 = new ManualId();
        manualId2.setProductId(1L);
        manualId2.setMaterialId(3L);

        ManualEntity manualEntity2 = new ManualEntity();
        manualEntity2.setId(manualId2);
        manualEntity2.setQuantity(20);

        return List.of(manualEntity1, manualEntity2);
    }
}

