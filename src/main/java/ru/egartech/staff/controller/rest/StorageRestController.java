package ru.egartech.staff.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.egartech.staff.api.StoragesApi;
import ru.egartech.staff.model.StorageInfoPagingResponseDto;
import ru.egartech.staff.model.StorageInfoResponseDto;
import ru.egartech.staff.model.StorageSaveRequestDto;
import ru.egartech.staff.model.StorageUpdateItemsRequestDto;
import ru.egartech.staff.service.StorageService;

@RestController
@RequiredArgsConstructor
public class StorageRestController implements StoragesApi {

    private final StorageService storageService;

    @Override
    public StorageInfoPagingResponseDto getAllStorages(Integer pageNumber, Integer pageSize,
                                                       String sortType, String sortFieldName) {
        return storageService.getAllStorages(pageNumber, pageSize, sortType, sortFieldName);
    }

    @Override
    public StorageInfoResponseDto getStorageById(Long storageId) {
        return storageService.getStorageById(storageId);
    }

    @Override
    public void createStorage(StorageSaveRequestDto storageSaveRequestDto) {
        storageService.createStorage(storageSaveRequestDto);
    }

    @Override
    public void updateStorageItemsInfoById(Long storageId, StorageUpdateItemsRequestDto requestDto) {
        storageService.updateStorageItemsInfoById(storageId, requestDto);
    }

    @Override
    public void updateStorage(Long storageId, StorageSaveRequestDto storageSaveRequestDto) {
        storageService.updateStorage(storageId, storageSaveRequestDto);
    }

    @Override
    public void deleteStorageById(Long storageId) {
        storageService.deleteStorageById(storageId);
    }
}
