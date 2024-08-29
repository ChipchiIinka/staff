package ru.egartech.staff.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.egartech.staff.entity.MaterialEntity;
import ru.egartech.staff.entity.ProductEntity;
import ru.egartech.staff.entity.StorageEntity;
import ru.egartech.staff.exception.ErrorType;
import ru.egartech.staff.exception.StaffException;
import ru.egartech.staff.model.*;
import ru.egartech.staff.repository.MaterialRepository;
import ru.egartech.staff.repository.ProductRepository;
import ru.egartech.staff.repository.StorageRepository;
import ru.egartech.staff.service.mapper.StorageMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StorageService {

    private final StorageRepository storageRepository;
    private final StorageMapper storageMapper;

    private final ProductRepository productRepository;
    private final MaterialRepository materialRepository;

    public StorageInfoPagingResponseDto getAllStorages(Integer pageNo, Integer pageSize,
                                                       String sortType, String sortFieldName) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortType), sortFieldName);
        PagingDto paging = new PagingDto();
        Page<StorageEntity> storageEntities = storageRepository.findAll(
                PageRequest.of(pageNo, pageSize, sort));
        paging.setPageNumber(pageNo);
        paging.setPageSize(pageSize);
        paging.setCount(storageEntities.getTotalElements());
        paging.setPages(storageEntities.getTotalPages());
        return new StorageInfoPagingResponseDto()
                .paging(paging)
                .content(storageMapper.toListDto(storageEntities));
    }

    public StorageInfoResponseDto getStorageById(Long storageId) {
        StorageEntity storageEntity = storageRepository.findById(storageId)
                .orElseThrow(() -> new StaffException(ErrorType.NOT_FOUND, "Склад не найден"));
        List<ProductStorageResponseDto> productsDto =
                storageMapper.toProductStorageListDto(getProductMapInfo(storageId));
        List<MaterialStorageResponseDto> materialsDto =
                storageMapper.toMaterialStorageListDto(getMaterialMapInfo(storageId));
        return storageMapper.toDto(storageEntity, productsDto, materialsDto);
    }

    public void createStorage(StorageSaveRequestDto storageSaveRequestDto) {
        StorageEntity storageEntity = new StorageEntity();
        storageRepository.save(storageMapper.toEntity(storageSaveRequestDto, storageEntity));
    }

    public Map<ProductEntity, Integer> getProductMapInfo(Long storageId) {
        List<Object[]> results = storageRepository.findAllProductsByStorageId(storageId);
        Map<ProductEntity, Integer> productsMap = new HashMap<>();
        for (Object[] result : results) {
            Long productId = (Long) result[0];
            ProductEntity product = productRepository.findById(productId)
                    .orElseThrow(() -> new StaffException(ErrorType.NOT_FOUND, "Товар не найден"));
            Integer available = (Integer) result[1];
            productsMap.put(product, available);
        }
        return productsMap;
    }

    public Map<MaterialEntity, Integer> getMaterialMapInfo(Long storageId) {
        List<Object[]> results = storageRepository.findAllMaterialsByStorageId(storageId);
        Map<MaterialEntity, Integer> materialsMap = new HashMap<>();
        for (Object[] result : results) {
            Long materialId = (Long) result[0];
            MaterialEntity material = materialRepository.findById(materialId)
                    .orElseThrow(() -> new StaffException(ErrorType.NOT_FOUND, "Материал не найден"));
            Integer available = (Integer) result[1];
            materialsMap.put(material, available);
        }
        return materialsMap;
    }

    @Transactional
    public void updateStorageItemsInfoById(Long storageId, StorageUpdateItemsRequestDto storageUpdateItemsRequestDto) {
        storageUpdateItemsRequestDto.getMaterials()
                .forEach(material -> storageRepository.addMaterialToStorage(
                        storageId, material.getId(), material.getQuantity()));
        storageUpdateItemsRequestDto.getProducts()
                .forEach(product -> storageRepository.addProductToStorage(
                        storageId, product.getId(), product.getQuantity()));
    }

    public void updateStorage(Long storageId, StorageSaveRequestDto storageSaveRequestDto) {
        StorageEntity storageEntity = storageRepository.findById(storageId)
                .orElseThrow(() -> new StaffException(ErrorType.NOT_FOUND, "Склад не найден"));
        storageRepository.save(storageMapper.toEntity(storageSaveRequestDto, storageEntity));
    }

    @Transactional
    public void deleteStorageById(Long storageId) {
        storageRepository.deleteStorageById(storageId);
    }
}
