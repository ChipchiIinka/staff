package ru.egartech.staff.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.egartech.staff.entity.MaterialEntity;
import ru.egartech.staff.entity.ProductEntity;
import ru.egartech.staff.entity.StorageEntity;
import ru.egartech.staff.model.*;
import ru.egartech.staff.repository.MaterialsRepository;
import ru.egartech.staff.repository.ProductsRepository;
import ru.egartech.staff.repository.StoragesRepository;
import ru.egartech.staff.service.mapper.StoragesMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StoragesService {

    private final StoragesRepository storagesRepository;
    private final StoragesMapper storagesMapper;

    private final ProductsRepository productsRepository;
    private final MaterialsRepository materialsRepository;

    public List<StorageListInfoResponseDto> getAllStorages() {
        return storagesMapper.toListDto(storagesRepository.findAll());
    }

    public StorageInfoResponseDto getStorageById(Long storageId) {
        StorageEntity storageEntity = storagesRepository.findById(storageId)
                .orElseThrow(() -> new RuntimeException("Storage not found")); //TODO временная заглушка, поменять на обработчик
        List<ProductStorageResponseDto> productsDto =
                storagesMapper.toProductStorageListDto(getProductMapInfo(storageId));
        List<MaterialStorageResponseDto> materialsDto =
                storagesMapper.toMaterialStorageListDto(getMaterialMapInfo(storageId));
        return storagesMapper.toDto(storageEntity, productsDto, materialsDto);
    }

    public void createStorage(StorageSaveRequestDto storageSaveRequestDto) {
        StorageEntity storageEntity = new StorageEntity();
        storagesRepository.save(storagesMapper.toEntity(storageSaveRequestDto, storageEntity));
    }

    public Map<ProductEntity, Integer> getProductMapInfo(Long storageId) {
        List<Object[]> results = storagesRepository.findAllProductsByStorageId(storageId);
        Map<ProductEntity, Integer> productsMap = new HashMap<>();
        for (Object[] result : results) {
            Long productId = (Long) result[0];
            ProductEntity product = productsRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found")); //TODO временная заглушка, поменять на обработчик
            Integer available = (Integer) result[1];
            productsMap.put(product, available);
        }
        return productsMap;
    }

    public Map<MaterialEntity, Integer> getMaterialMapInfo(Long storageId) {
        List<Object[]> results = storagesRepository.findAllMaterialsByStorageId(storageId);
        Map<MaterialEntity, Integer> materialsMap = new HashMap<>();
        for (Object[] result : results) {
            Long materialId = (Long) result[0];
            MaterialEntity material = materialsRepository.findById(materialId)
                    .orElseThrow(() -> new RuntimeException("Material not found")); //TODO временная заглушка, поменять на обработчик
            Integer available = (Integer) result[1];
            materialsMap.put(material, available);
        }
        return materialsMap;
    }
}
