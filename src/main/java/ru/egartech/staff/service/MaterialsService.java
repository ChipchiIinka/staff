package ru.egartech.staff.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.egartech.staff.entity.MaterialEntity;
import ru.egartech.staff.model.MaterialInfoResponseDto;
import ru.egartech.staff.model.MaterialListInfoResponseDto;
import ru.egartech.staff.model.MaterialSaveRequestDto;
import ru.egartech.staff.repository.MaterialsRepository;
import ru.egartech.staff.repository.StoragesRepository;
import ru.egartech.staff.service.mapper.MaterialsMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MaterialsService {
    private final MaterialsRepository materialsRepository;
    private final MaterialsMapper materialsMapper;

    private final StoragesRepository storagesRepository;

    public List<MaterialListInfoResponseDto> getAllMaterials() {
        return materialsMapper.toListDto(materialsRepository.findAll());
    }

    public MaterialInfoResponseDto getMaterialById(Long materialId) {
        Long availableQuantity = storagesRepository.findAvailableByMaterialId(materialId);
        MaterialEntity material = materialsRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("Material not found")); //TODO временная заглушка, поменять на обработчик
        return materialsMapper.toDto(material, availableQuantity);
    }

    public void createMaterial(MaterialSaveRequestDto materialDto) {
        MaterialEntity material = new MaterialEntity();
        materialsRepository.save(materialsMapper.toEntity(materialDto, material));
    }

    public void updateMaterial(Long id, MaterialSaveRequestDto materialDto) {
        MaterialEntity material = materialsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material not found")); //TODO временная заглушка, поменять на обработчик
        materialsRepository.save(materialsMapper.toEntity(materialDto, material));
    }
}
