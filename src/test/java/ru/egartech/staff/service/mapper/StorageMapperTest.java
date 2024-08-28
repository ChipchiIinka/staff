package ru.egartech.staff.service.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.egartech.staff.entity.MaterialEntity;
import ru.egartech.staff.entity.ProductEntity;
import ru.egartech.staff.entity.StorageEntity;
import ru.egartech.staff.entity.enums.MaterialType;
import ru.egartech.staff.entity.enums.ProductType;
import ru.egartech.staff.model.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = StorageMapperImpl.class)
class StorageMapperTest {

    @Autowired
    private StorageMapper storageMapper;

    @Test
    void testToListDto() {
        StorageEntity storageEntity1 = createStorageEntity(1L);
        StorageEntity storageEntity2 = createStorageEntity(2L);

        List<StorageEntity> storageList = List.of(storageEntity1, storageEntity2);
        Page<StorageEntity> storagePage = new PageImpl<>(storageList, PageRequest.of(0, 10), storageList.size());

        List<StorageListInfoResponseDto> result = storageMapper.toListDto(storagePage);

        assertEquals(2, result.size());
        assertEquals(storageEntity1.getId(), result.get(0).getId());
        assertEquals(storageEntity2.getId(), result.get(1).getId());
        assertEquals(storageEntity1.getAddress(), result.get(0).getAddress());
        assertEquals(storageEntity2.getAddress(), result.get(1).getAddress());
    }

    @Test
    void testToDto(){
        StorageEntity storageEntity1 = createStorageEntity(1L);

        StorageInfoResponseDto storageInfoResponseDto = storageMapper.toDto(storageEntity1, List.of(), List.of());

        assertEquals(storageEntity1.getId(), storageInfoResponseDto.getId());
        assertEquals(storageEntity1.getAddress(), storageInfoResponseDto.getAddress());
        assertEquals(List.of(), storageInfoResponseDto.getProducts());
        assertEquals(List.of(), storageInfoResponseDto.getMaterials());
    }

    @Test
    void testToEntity() {
        StorageSaveRequestDto storageSaveRequestDto = new StorageSaveRequestDto()
                .city("Город N")
                .street("ул.Уличная")
                .house("1");

        StorageEntity storageEntity = new StorageEntity();
        storageEntity.setId(1L);
        storageMapper.toEntity(storageSaveRequestDto, storageEntity);

        assertNotNull(storageEntity);
        assertEquals(1L, storageEntity.getId());
        assertEquals("Город N, ул.Уличная, 1", storageEntity.getAddress());
    }

    @Test
    void testToProductStorageListDto() {
        Map<ProductEntity, Integer> productEntityIntegerMap = new HashMap<>();
        productEntityIntegerMap.put(getProductEntity(1L), 10);

        List<ProductStorageResponseDto> result = storageMapper.toProductStorageListDto(productEntityIntegerMap);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Товар 1", result.get(0).getName());
        assertEquals(ProductTypeDto.OTHER, result.get(0).getType());
        assertEquals(10, result.get(0).getQuantity());
    }

    @Test
    void testToMaterialStorageListDto() {
        Map<MaterialEntity, Integer> materialEntityIntegerMap = new HashMap<>();
        materialEntityIntegerMap.put(getMaterialEntity(1L), 11);

        List<MaterialStorageResponseDto> result = storageMapper.toMaterialStorageListDto(materialEntityIntegerMap);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Материал 1", result.get(0).getName());
        assertEquals(MaterialTypeDto.OTHER, result.get(0).getType());
        assertEquals("10 / 20 / 30", result.get(0).getShortInfo());
        assertEquals(11, result.get(0).getQuantity());
    }

    private static StorageEntity createStorageEntity(Long id) {
        StorageEntity storageEntity = new StorageEntity();
        storageEntity.setId(id);
        storageEntity.setAddress("Город N, ул.Уличная, " + id);
        return storageEntity;
    }

    private static ProductEntity getProductEntity(Long productId) {
        ProductEntity product = new ProductEntity();
        product.setId(productId);
        product.setName("Товар " + productId);
        product.setDescription("Описание товара " + product);
        product.setPrice(BigDecimal.valueOf(productId * 100));
        product.setType(ProductType.OTHER);
        return product;
    }

    private static MaterialEntity getMaterialEntity(Long materialId) {
        MaterialEntity material = new MaterialEntity();
        material.setId(materialId);
        material.setName("Материал " + materialId);
        material.setDescription("Описание материала " + material);
        material.setLength((int) (materialId * 10));
        material.setWidth((int) (materialId * 20));
        material.setHeight((int) (materialId * 30));
        material.setType(MaterialType.OTHER);
        return material;
    }
}