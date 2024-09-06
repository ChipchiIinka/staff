package ru.egartech.staff.service;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import ru.egartech.staff.entity.MaterialEntity;
import ru.egartech.staff.model.*;
import ru.egartech.staff.repository.MaterialRepository;
import ru.egartech.staff.repository.StorageRepository;
import ru.egartech.staff.service.mapper.MaterialMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaterialServiceTest {

    @Mock
    MaterialRepository materialRepository;

    @Mock
    MaterialMapper materialMapper;

    @Mock
    StorageRepository storageRepository;

    @InjectMocks
    MaterialService materialService;

    private MaterialEntity material;
    private MaterialListInfoResponseDto materialListInfoResponseDto;
    private MaterialInfoResponseDto materialInfoResponseDto;
    private MaterialSaveRequestDto materialSaveRequestDto;

    @BeforeEach
    void setUp() {
        material = new MaterialEntity();
        materialListInfoResponseDto = new MaterialListInfoResponseDto();
        materialInfoResponseDto = new MaterialInfoResponseDto();
        materialSaveRequestDto = new MaterialSaveRequestDto();
    }

    @Test
    @SneakyThrows
    void testGetAllMaterials() {
        PageRequest pageRequest = PageRequest.of(1, 2, Sort.by(Sort.Direction.ASC, "id"));

        Page<MaterialEntity> dbMaterials = new PageImpl<>(List.of(material), pageRequest, 3);
        List<MaterialListInfoResponseDto> materialDtos = List.of(materialListInfoResponseDto);

        when(materialRepository.findAll(ArgumentMatchers.any(Specification.class), ArgumentMatchers.eq(pageRequest))).thenReturn(dbMaterials);
        when(materialMapper.toListDto(dbMaterials)).thenReturn(materialDtos);

        MaterialInfoPagingResponseDto response = materialService.getAllMaterials(1, 2, "asc", "id", "");

        assertEquals(1, response.getContent().size());
        assertEquals(2, response.getPaging().getPages());
        assertEquals(2, response.getPaging().getPageSize());
        assertEquals(1, response.getPaging().getPageNumber());
        assertEquals(3, response.getPaging().getCount());
        assertEquals(materialDtos, response.getContent());
    }

    @Test
    void testGetMaterialById() {
        Long materialId = 1L;
        Long availableQuantity = 10L;

        materialInfoResponseDto.setName("Material 1");
        materialInfoResponseDto.setAvailable(availableQuantity);

        when(materialRepository.findById(materialId)).thenReturn(Optional.of(material));
        when(storageRepository.findAvailableByMaterialId(materialId)).thenReturn(availableQuantity);
        when(materialMapper.toDto(material, availableQuantity)).thenReturn(materialInfoResponseDto);

        MaterialInfoResponseDto result = materialService.getMaterialById(materialId);

        assertNotNull(result);
        assertEquals("Material 1", result.getName());
        assertEquals(availableQuantity, result.getAvailable());
        verify(materialRepository, times(1)).findById(materialId);
        verify(storageRepository, times(1)).findAvailableByMaterialId(materialId);
    }

    @Test
    void testCreateMaterial() {
        when(materialMapper.toEntity(materialSaveRequestDto, material)).thenReturn(material);
        when(materialRepository.save(material)).thenReturn(material);

        materialService.createMaterial(materialSaveRequestDto);

        verify(materialRepository, times(1)).save(material);
    }

    @Test
    void testUpdateMaterial() {
        Long materialId = 1L;
        materialSaveRequestDto.setName("Updated Material");
        materialSaveRequestDto.setDescription("Updated Description");
        materialSaveRequestDto.setType(MaterialTypeDto.OTHER);
        materialSaveRequestDto.setLength(15);
        materialSaveRequestDto.setWidth(25);
        materialSaveRequestDto.setHeight(35);

        material.setName("Existing Material");

        when(materialRepository.findById(materialId)).thenReturn(Optional.of(material));
        when(materialMapper.toEntity(materialSaveRequestDto, material)).thenReturn(material);
        when(materialRepository.save(material)).thenReturn(material);

        materialService.updateMaterial(materialId, materialSaveRequestDto);

        verify(materialRepository, times(1)).findById(materialId);
        verify(materialMapper, times(1)).toEntity(materialSaveRequestDto, material);
        verify(materialRepository, times(1)).save(material);
    }

    @Test
    void testDeleteMaterialById() {
        Long materialId = 1L;

        materialService.deleteMaterialById(materialId);

        verify(materialRepository, times(1)).deleteById(materialId);
    }
}