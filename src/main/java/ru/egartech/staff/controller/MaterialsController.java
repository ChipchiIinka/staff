package ru.egartech.staff.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.egartech.staff.api.MaterialsApi;
import ru.egartech.staff.model.MaterialInfoResponseDto;
import ru.egartech.staff.model.MaterialListInfoResponseDto;
import ru.egartech.staff.model.MaterialSaveRequestDto;
import ru.egartech.staff.service.MaterialsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MaterialsController implements MaterialsApi {

    private final MaterialsService materialsService;

    @Override
    public List<MaterialListInfoResponseDto> getAllMaterials() {
        return materialsService.getAllMaterials();
    }

    @Override
    public MaterialInfoResponseDto getMaterialById(Long materialId) {
        return materialsService.getMaterialById(materialId);
    }

    @Override
    public void createMaterial(MaterialSaveRequestDto materialSaveRequestDto) {
        materialsService.createMaterial(materialSaveRequestDto);
    }

    @Override
    public void updateMaterial(Long materialId, MaterialSaveRequestDto materialSaveRequestDto) {
        materialsService.updateMaterial(materialId, materialSaveRequestDto);
    }
}
