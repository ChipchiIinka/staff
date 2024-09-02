package ru.egartech.staff.service.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.egartech.staff.entity.StorageEntity;
import ru.egartech.staff.entity.projection.MaterialStorageProjection;
import ru.egartech.staff.entity.projection.ProductStorageProjection;
import ru.egartech.staff.model.*;

import java.util.List;

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
        ProductStorageProjection storageProjection = new ProductStorageProjection() {
            @Override
            public Long getProductId() {
                return 1L;
            }
            @Override
            public Integer getAvailable() {
                return 10;
            }
        };

        List<ProductStorageInfoDto> result = storageMapper.toProductStorageListDto(List.of(storageProjection));

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(10, result.get(0).getQuantity());
    }

    @Test
    void testToMaterialStorageListDto() {
        MaterialStorageProjection storageProjection = new MaterialStorageProjection() {
            @Override
            public Long getMaterialId() {
                return 1L;
            }
            @Override
            public Integer getAvailable() {
                return 11;
            }
        };

        List<MaterialStorageInfoDto> result = storageMapper.toMaterialStorageListDto(List.of(storageProjection));

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(11, result.get(0).getQuantity());
    }

    private static StorageEntity createStorageEntity(Long id) {
        StorageEntity storageEntity = new StorageEntity();
        storageEntity.setId(id);
        storageEntity.setAddress("Город N, ул.Уличная, " + id);
        return storageEntity;
    }
}