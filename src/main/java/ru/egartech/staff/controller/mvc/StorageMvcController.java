package ru.egartech.staff.controller.mvc;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.egartech.staff.model.StorageInfoPagingResponseDto;
import ru.egartech.staff.model.StorageInfoResponseDto;
import ru.egartech.staff.model.StorageSaveRequestDto;
import ru.egartech.staff.model.StorageUpdateItemsRequestDto;
import ru.egartech.staff.service.MaterialService;
import ru.egartech.staff.service.ProductService;
import ru.egartech.staff.service.StorageService;

import java.util.function.Function;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/storages")
public class StorageMvcController {

    private static final String MODEL_ATTRIBUTE_STORAGE = "storage";
    private static final String REDIRECT_TO_STORAGES = "redirect:/api/storages";
    private static final String REDIRECT_TO_STORAGES_WITH_ID = "redirect:/api/storages/";

    private final StorageService storageService;
    private final MaterialService materialService;
    private final ProductService productService;

    @GetMapping
    public String getAllStorages(@RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
                                 @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                 @RequestParam(value = "sortType", required = false, defaultValue = "asc") String sortType,
                                 @RequestParam(value = "sortFieldName", required = false, defaultValue = "id") String sortFieldName,
                                 Model model) {
        StorageInfoPagingResponseDto response = storageService.getAllStorages(pageNumber, pageSize, sortType, sortFieldName);
        model.addAttribute("storages", response);
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("sortType", sortType);
        model.addAttribute("sortFieldName", sortFieldName);
        model.addAttribute("sortLink", (Function<String, String>) field ->
                storageService.generateSortLink(field, sortFieldName, sortType, pageNumber, pageSize));
        return "storages/list";
    }

    @GetMapping("/{storageId}")
    public String getStorageById(@PathVariable Long storageId, Model model) {
        StorageInfoResponseDto response = storageService.getStorageById(storageId);
        model.addAttribute(MODEL_ATTRIBUTE_STORAGE, response);
        return "storages/info";
    }


    @GetMapping("/create")
    public String showCreateStorageForm(Model model) {
        model.addAttribute(MODEL_ATTRIBUTE_STORAGE, new StorageSaveRequestDto());
        return "storages/create";
    }

    @PostMapping("/create")
    public String createStorage(@ModelAttribute(MODEL_ATTRIBUTE_STORAGE) @Valid StorageSaveRequestDto storageSaveRequestDto,
                                BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "storages/create";
        }
        storageService.createStorage(storageSaveRequestDto);
        return REDIRECT_TO_STORAGES;
    }


    @GetMapping("/{storageId}/update/items")
    public String showUpdateStorageItemsForm(@PathVariable Long storageId, Model model) {
        model.addAttribute(MODEL_ATTRIBUTE_STORAGE, storageService.getStorageById(storageId));
        return "storages/update-items";
    }

    @PatchMapping("/{storageId}/update/items")
    public String updateStorageItemsInfoById(@PathVariable Long storageId,
                                             @ModelAttribute(MODEL_ATTRIBUTE_STORAGE) @Valid StorageUpdateItemsRequestDto requestDto,
                                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "storages/update-items";
        }
        storageService.updateStorageItemsInfoById(storageId, requestDto);
        return REDIRECT_TO_STORAGES_WITH_ID + storageId;
    }

    @GetMapping("/{storageId}/update")
    public String showUpdateStorageForm(@PathVariable Long storageId, Model model) {
        model.addAttribute(MODEL_ATTRIBUTE_STORAGE, storageService.getStorageDataById(storageId));
        return "storages/update";
    }

    @PutMapping("/{storageId}/update")
    public String updateStorage(@PathVariable Long storageId,
                                @ModelAttribute(MODEL_ATTRIBUTE_STORAGE) @Valid StorageSaveRequestDto storageSaveRequestDto,
                                BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            return "storages/update";
        }
        storageService.updateStorage(storageId, storageSaveRequestDto);
        return REDIRECT_TO_STORAGES;
    }


    @DeleteMapping("/{storageId}/delete")
    public String deleteStorageById(@PathVariable Long storageId) {
        storageService.deleteStorageById(storageId);
        return REDIRECT_TO_STORAGES;
    }
}
