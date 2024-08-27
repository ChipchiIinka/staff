package ru.egartech.staff.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.egartech.staff.entity.MaterialEntity;
import ru.egartech.staff.exception.ErrorType;
import ru.egartech.staff.exception.StaffException;
import ru.egartech.staff.model.*;
import ru.egartech.staff.repository.MaterialRepository;
import ru.egartech.staff.repository.StorageRepository;
import ru.egartech.staff.service.mapper.MaterialMapper;

@Service
@RequiredArgsConstructor
public class MaterialService {
    private final MaterialRepository materialRepository;
    private final MaterialMapper materialMapper;

    private final StorageRepository storageRepository;

    public MaterialInfoPagingResponseDto getAllMaterials(Integer pageNo, Integer pageSize,
                                                         String sortType, String sortFieldName) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortType), sortFieldName);
        PagingDto paging = new PagingDto();
        Page<MaterialEntity> materialEntities = materialRepository.findAll(
                PageRequest.of(pageNo, pageSize, sort));
        paging.setPageNumber(pageNo);
        paging.setPageSize(pageSize);
        paging.setCount(materialEntities.getTotalElements());
        paging.setPages(materialEntities.getTotalPages());
        return new MaterialInfoPagingResponseDto()
                .paging(paging)
                .content(materialMapper.toListDto(materialEntities));
    }

    public MaterialInfoResponseDto getMaterialById(Long materialId) {
        Long availableQuantity = storageRepository.findAvailableByMaterialId(materialId);
        MaterialEntity material = materialRepository.findById(materialId)
                .orElseThrow(() -> new StaffException(ErrorType.NOT_FOUND, "Материал не найден"));
        return materialMapper.toDto(material, availableQuantity);
    }

    public void createMaterial(MaterialSaveRequestDto materialDto) {
        MaterialEntity material = new MaterialEntity();
        materialRepository.save(materialMapper.toEntity(materialDto, material));
    }

    public void updateMaterial(Long id, MaterialSaveRequestDto materialDto) {
        MaterialEntity material = materialRepository.findById(id)
                .orElseThrow(() -> new StaffException(ErrorType.NOT_FOUND, "Материал не найден"));
        materialRepository.save(materialMapper.toEntity(materialDto, material));
    }

    public void deleteMaterialById(Long materialId) {
        materialRepository.deleteById(materialId);
    }
}
