package ru.egartech.staff.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.egartech.staff.entity.StorageEntity;
import ru.egartech.staff.exception.ErrorType;
import ru.egartech.staff.exception.StaffException;
import ru.egartech.staff.model.*;
import ru.egartech.staff.repository.StorageRepository;
import ru.egartech.staff.service.mapper.StorageMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StorageService {

    private final StorageRepository storageRepository;
    private final StorageMapper storageMapper;

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

    @Transactional
    public StorageInfoResponseDto getStorageById(Long storageId) {
        StorageEntity storageEntity = storageRepository.findById(storageId)
                .orElseThrow(() -> new StaffException(ErrorType.NOT_FOUND, "Склад не найден"));
        List<ProductStorageInfoDto> productsDto =
                storageMapper.toProductStorageListDto(storageRepository.findAllProductsByStorageId(storageId));
        List<MaterialStorageInfoDto> materialsDto =
                storageMapper.toMaterialStorageListDto(storageRepository.findAllMaterialsByStorageId(storageId));
        return storageMapper.toDto(storageEntity, productsDto, materialsDto);
    }

    public void createStorage(StorageSaveRequestDto storageSaveRequestDto) {
        StorageEntity storageEntity = new StorageEntity();
        storageRepository.save(storageMapper.toEntity(storageSaveRequestDto, storageEntity));
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
        storageRepository.deleteById(storageId);
    }
}
