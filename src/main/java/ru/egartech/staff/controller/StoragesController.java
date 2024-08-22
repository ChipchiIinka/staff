package ru.egartech.staff.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.egartech.staff.api.StoragesApi;
import ru.egartech.staff.model.StorageInfoResponseDto;
import ru.egartech.staff.model.StorageListInfoResponseDto;
import ru.egartech.staff.model.StorageSaveRequestDto;
import ru.egartech.staff.service.StoragesService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StoragesController implements StoragesApi {

    private final StoragesService storagesService;

    @Override
    public List<StorageListInfoResponseDto> getAllStorages() {
        return storagesService.getAllStorages();
    }

    @Override
    public StorageInfoResponseDto getStorageById(Long storageId) {
        return storagesService.getStorageById(storageId);
    }

    @Override
    public void createStorage(StorageSaveRequestDto storageSaveRequestDto) {
        storagesService.createStorage(storageSaveRequestDto);
    }
}
