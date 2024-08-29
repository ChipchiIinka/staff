package ru.egartech.staff.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.egartech.staff.entity.MaterialEntity;
import ru.egartech.staff.entity.ProductEntity;
import ru.egartech.staff.entity.StorageEntity;
import ru.egartech.staff.exception.StaffException;
import ru.egartech.staff.model.*;
import ru.egartech.staff.repository.MaterialRepository;
import ru.egartech.staff.repository.ProductRepository;
import ru.egartech.staff.repository.StorageRepository;
import ru.egartech.staff.service.mapper.StorageMapper;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StorageServiceTest {

    @Mock
    private StorageRepository storageRepository;

    @Mock
    private StorageMapper storageMapper;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MaterialRepository materialRepository;

    @InjectMocks
    private StorageService storageService;

    private StorageEntity storageEntity;
    private StorageInfoResponseDto storageInfoResponseDto;
    private StorageSaveRequestDto storageSaveRequestDto;
    private ProductEntity productEntity;
    private MaterialEntity materialEntity;

    @BeforeEach
    void setUp() {
        storageEntity = new StorageEntity();
        storageInfoResponseDto = new StorageInfoResponseDto();
        storageSaveRequestDto = new StorageSaveRequestDto();
        productEntity = new ProductEntity();
        materialEntity = new MaterialEntity();
    }

    @Test
    void testGetAllStorages() {
        PageRequest pageRequest = PageRequest.of(1, 2, Sort.by(Sort.Direction.ASC, "id"));
        Page<StorageEntity> page = new PageImpl<>(List.of(storageEntity), pageRequest, 3);

        when(storageRepository.findAll(pageRequest)).thenReturn(page);
        when(storageMapper.toListDto(page)).thenReturn(List.of(new StorageListInfoResponseDto()));

        StorageInfoPagingResponseDto result = storageService.getAllStorages(1, 2, "ASC", "id");

        assertEquals(1, result.getContent().size());
        assertEquals(3L, result.getPaging().getCount());
        verify(storageRepository, times(1)).findAll(pageRequest);
        verify(storageMapper, times(1)).toListDto(page);
    }

    @Test
    void testGetStorageById() {
        Long storageId = 1L;
        productEntity.setId(storageId);
        materialEntity.setId(storageId);
        Object[] productData = new Object[]{1L, 10};
        Object[] materialData = new Object[]{1L, 10};

        Map<ProductEntity, Integer> productsMap = Map.of(productEntity, 10);
        Map<MaterialEntity, Integer> materialsMap = Map.of(materialEntity, 10);
        List<ProductStorageResponseDto> productsDto = List.of(new ProductStorageResponseDto());
        List<MaterialStorageResponseDto> materialsDto = List.of(new MaterialStorageResponseDto());

        when(storageRepository.findById(storageId)).thenReturn(Optional.of(storageEntity));
        when(storageRepository.findAllProductsByStorageId(storageId)).thenReturn(List.<Object[]>of(productData));
        when(storageRepository.findAllMaterialsByStorageId(storageId)).thenReturn(List.<Object[]>of(materialData));
        when(productRepository.findById(storageId)).thenReturn(Optional.ofNullable(productEntity));
        when(materialRepository.findById(storageId)).thenReturn(Optional.ofNullable(materialEntity));
        when(storageMapper.toProductStorageListDto(productsMap)).thenReturn(productsDto);
        when(storageMapper.toMaterialStorageListDto(materialsMap)).thenReturn(materialsDto);
        when(storageMapper.toDto(storageEntity, productsDto, materialsDto)).thenReturn(storageInfoResponseDto);

        StorageInfoResponseDto result = storageService.getStorageById(storageId);

        assertEquals(storageInfoResponseDto, result);
        verify(storageRepository, times(1)).findById(storageId);
        verify(storageMapper, times(1)).toDto(storageEntity, productsDto, materialsDto);
    }

    @Test
    void testGetStorageByIdThrowsExceptionWhenNotFound() {
        Long storageId = 1L;
        when(storageRepository.findById(storageId)).thenReturn(Optional.empty());

        assertThrows(StaffException.class, () -> storageService.getStorageById(storageId));
    }

    @Test
    void testCreateStorage() {
        when(storageMapper.toEntity(storageSaveRequestDto, storageEntity)).thenReturn(storageEntity);

        storageService.createStorage(storageSaveRequestDto);

        verify(storageRepository, times(1)).save(storageEntity);
    }

    @Test
    void testUpdateStorage() {
        Long storageId = 1L;
        when(storageRepository.findById(storageId)).thenReturn(Optional.of(storageEntity));
        when(storageMapper.toEntity(storageSaveRequestDto, storageEntity)).thenReturn(storageEntity);

        storageService.updateStorage(storageId, storageSaveRequestDto);

        verify(storageRepository, times(1)).save(storageEntity);
    }

    @Test
    void testDeleteStorageById() {
        Long storageId = 1L;

        storageService.deleteStorageById(storageId);

        verify(storageRepository, times(1)).deleteById(storageId);
    }

    // Тесты для методов getProductMapInfo и getMaterialMapInfo
    @Test
    void testGetProductMapInfo() {
        Long storageId = 1L;
        productEntity.setId(1L);
        Object[] productData = new Object[]{1L, 10};

        when(storageRepository.findAllProductsByStorageId(storageId)).thenReturn(List.<Object[]>of(productData));
        when(productRepository.findById(1L)).thenReturn(Optional.of(productEntity));

        Map<ProductEntity, Integer> result = storageService.getProductMapInfo(storageId);

        assertEquals(1, result.size());
        assertEquals(10, result.get(productEntity));
    }

    @Test
    void testGetMaterialMapInfo() {
        Long storageId = 1L;
        materialEntity.setId(1L);
        Object[] materialData = new Object[]{1L, 10};

        when(storageRepository.findAllMaterialsByStorageId(storageId)).thenReturn(List.<Object[]>of(materialData));
        when(materialRepository.findById(1L)).thenReturn(Optional.of(materialEntity));

        Map<MaterialEntity, Integer> result = storageService.getMaterialMapInfo(storageId);

        assertEquals(1, result.size());
        assertEquals(10, result.get(materialEntity));
    }
}

