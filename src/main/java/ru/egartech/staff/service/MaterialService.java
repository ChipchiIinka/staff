package ru.egartech.staff.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.egartech.staff.cache.Caches;
import ru.egartech.staff.entity.MaterialEntity;
import ru.egartech.staff.exception.ErrorType;
import ru.egartech.staff.exception.StaffException;
import ru.egartech.staff.model.MaterialInfoPagingResponseDto;
import ru.egartech.staff.model.MaterialInfoResponseDto;
import ru.egartech.staff.model.MaterialSaveRequestDto;
import ru.egartech.staff.model.PagingDto;
import ru.egartech.staff.repository.MaterialRepository;
import ru.egartech.staff.repository.StorageRepository;
import ru.egartech.staff.repository.specification.MaterialSpecification;
import ru.egartech.staff.service.mapper.MaterialMapper;

@Service
@RequiredArgsConstructor
public class MaterialService {
    private final MaterialRepository materialRepository;
    private final MaterialMapper materialMapper;

    private final StorageRepository storageRepository;

    @Cacheable(Caches.MATERIALS_CACHE)
    public MaterialInfoPagingResponseDto getAllMaterials(Integer pageNo, Integer pageSize,
                                                         String sortType, String sortFieldName, String searchingFilter) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortType), sortFieldName);
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, sort);
        Specification<MaterialEntity> materialSpecification = Specification
                .where(MaterialSpecification.hasName(searchingFilter))
                .or(MaterialSpecification.hasType(searchingFilter));
        Page<MaterialEntity> materialEntities = materialRepository.findAll(materialSpecification, pageRequest);
        PagingDto paging = new PagingDto()
                .pageNumber(pageNo)
                .pageSize(pageSize)
                .count(materialEntities.getTotalElements())
                .pages(materialEntities.getTotalPages());
        return new MaterialInfoPagingResponseDto()
                .paging(paging)
                .content(materialMapper.toListDto(materialEntities));
    }

    @Cacheable(value = Caches.MATERIALS_CACHE, key = "#materialId")
    public MaterialInfoResponseDto getMaterialById(Long materialId) {
        Long availableQuantity = storageRepository.findAvailableByMaterialId(materialId);
        if (availableQuantity == null) {
            availableQuantity = 0L;
        }
        MaterialEntity material = materialRepository.findById(materialId)
                .orElseThrow(() -> new StaffException(ErrorType.NOT_FOUND, "Материал не найден"));
        return materialMapper.toDto(material, availableQuantity);
    }

    @CacheEvict(value = Caches.MATERIALS_CACHE, allEntries = true)
    public void createMaterial(MaterialSaveRequestDto materialDto) {
        MaterialEntity material = new MaterialEntity();
        materialRepository.save(materialMapper.toEntity(materialDto, material));
    }

    @CacheEvict(value = Caches.MATERIALS_CACHE, allEntries = true)
    public void updateMaterial(Long materialId, MaterialSaveRequestDto materialDto) {
        MaterialEntity material = materialRepository.findById(materialId)
                .orElseThrow(() -> new StaffException(ErrorType.NOT_FOUND, "Материал не найден"));
        materialRepository.save(materialMapper.toEntity(materialDto, material));
    }

    @CacheEvict(value = Caches.MATERIALS_CACHE, allEntries = true)
    public void deleteMaterialById(Long materialId) {
        materialRepository.deleteById(materialId);
    }

    public String generateSortLink(String field, String currentSortField, String currentSortType, int pageNumber,
                                   int pageSize, String searchingFilter) {
        String newSortType = "asc".equals(currentSortType) && field.equals(currentSortField) ? "desc" : "asc";
        return String.format("/api/materials?pageNumber=%d&pageSize=%d&sortFieldName=%s&sortType=%s&searchingFilter=%s",
                pageNumber, pageSize, field, newSortType, searchingFilter);
    }
}
