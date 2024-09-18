package ru.egartech.staff.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.egartech.staff.cache.Caches;
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

    /**
     * Получение списка всех складов с постраничной навигацией и сортировкой
     *
     * @param pageNo      Номер страницы
     * @param pageSize    Размер страницы
     * @param sortType    Тип сортировки (asc/desc)
     * @param sortFieldName Поле для сортировки
     * @return Страница складов с информацией о постраничной навигации
     */
    @Cacheable(Caches.STORAGES_CACHE)
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

    /**
     * Получение информации о складе по его идентификатору
     *
     * @param storageId Идентификатор склада
     * @return Детальная информация о складе, товарах и материалах
     */
    @Cacheable(value = Caches.STORAGES_CACHE, key = "#storageId")
    public StorageInfoResponseDto getStorageById(Long storageId) {
        StorageEntity storageEntity = storageRepository.findById(storageId)
                .orElseThrow(() -> new StaffException(ErrorType.NOT_FOUND, "Склад не найден с id=" + storageId));
        List<ProductStorageInfoDto> productsDto =
                storageMapper.toProductStorageListDto(storageRepository.findAllProductsByStorageId(storageId));
        List<MaterialStorageInfoDto> materialsDto =
                storageMapper.toMaterialStorageListDto(storageRepository.findAllMaterialsByStorageId(storageId));
        return storageMapper.toDto(storageEntity, productsDto, materialsDto);
    }

    /**
     * Создание нового склада
     *
     * @param storageSaveRequestDto Данные для создания нового склада
     */
    @CacheEvict(value = Caches.STORAGES_CACHE, allEntries = true)
    public void createStorage(StorageSaveRequestDto storageSaveRequestDto) {
        StorageEntity storageEntity = new StorageEntity();
        storageRepository.save(storageMapper.toEntity(storageSaveRequestDto, storageEntity));
    }

    /**
     * Обновление информации о товарах и материалах на складе
     *
     * @param storageId                    Идентификатор склада
     * @param storageUpdateItemsRequestDto DTO с новой информацией о товарах и материалах
     */
    @Transactional
    @CacheEvict(value = Caches.STORAGES_CACHE, key = "#storageId")
    public void updateStorageItemsInfoById(Long storageId, StorageUpdateItemsRequestDto storageUpdateItemsRequestDto) {
        storageUpdateItemsRequestDto.getMaterials()
                .forEach(material -> storageRepository.addMaterialToStorage(
                        storageId, material.getId(), material.getQuantity()));
        storageUpdateItemsRequestDto.getProducts()
                .forEach(product -> storageRepository.addProductToStorage(
                        storageId, product.getId(), product.getQuantity()));
    }

    /**
     * Обновление информации о складе
     *
     * @param storageId            Идентификатор склада
     * @param storageSaveRequestDto DTO с обновленными данными склада
     */
    @CacheEvict(value = Caches.STORAGES_CACHE, allEntries = true)
    public void updateStorage(Long storageId, StorageSaveRequestDto storageSaveRequestDto) {
        StorageEntity storageEntity = storageRepository.findById(storageId)
                .orElseThrow(() -> new StaffException(ErrorType.NOT_FOUND, "Склад не найден"));
        storageRepository.save(storageMapper.toEntity(storageSaveRequestDto, storageEntity));
    }

    /**
     * Удаление склада по его идентификатору
     *
     * @param storageId Идентификатор склада
     */
    @CacheEvict(value = Caches.STORAGES_CACHE, allEntries = true)
    public void deleteStorageById(Long storageId) {
        storageRepository.deleteById(storageId);
    }

    /**
     * Получение данных о сохраненном складе для редактирования
     *
     * @param storageId Уникальный идентификатор склада
     */
    public StorageSaveRequestDto getStorageDataById(Long storageId) {
        StorageEntity storageEntity = storageRepository.findById(storageId)
                .orElseThrow(() -> new StaffException(ErrorType.NOT_FOUND, "Склад не найден"));
        return new StorageSaveRequestDto()
                .city(storageEntity.getAddress().split(",")[0])
                .street(storageEntity.getAddress().split(",")[1])
                .house(storageEntity.getAddress().split(",")[2]);
    }
}
