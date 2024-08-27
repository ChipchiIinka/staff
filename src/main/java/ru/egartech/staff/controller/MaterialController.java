package ru.egartech.staff.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.egartech.staff.api.MaterialsApi;
import ru.egartech.staff.model.MaterialInfoPagingResponseDto;
import ru.egartech.staff.model.MaterialInfoResponseDto;
import ru.egartech.staff.model.MaterialSaveRequestDto;
import ru.egartech.staff.service.MaterialService;

@RestController
@RequiredArgsConstructor
public class MaterialController implements MaterialsApi {

    private final MaterialService materialService;

    @Override
    public MaterialInfoPagingResponseDto getAllMaterials(Integer pageNumber, Integer pageSize,
                                                         String sortType, String sortFieldName) {
        return materialService.getAllMaterials(pageNumber, pageSize, sortType, sortFieldName);
    }

    @Override
    public MaterialInfoResponseDto getMaterialById(Long materialId) {
        return materialService.getMaterialById(materialId);
    }

    @Override
    public void createMaterial(MaterialSaveRequestDto materialSaveRequestDto) {
        materialService.createMaterial(materialSaveRequestDto);
    }

    @Override
    public void updateMaterial(Long materialId, MaterialSaveRequestDto materialSaveRequestDto) {
        materialService.updateMaterial(materialId, materialSaveRequestDto);
    }

    @Override
    public void deleteMaterialById(Long materialId) {
        materialService.deleteMaterialById(materialId);
    }
}
