package ru.egartech.staff.controller.mvc;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.egartech.staff.model.MaterialInfoResponseDto;
import ru.egartech.staff.model.MaterialSaveRequestDto;
import ru.egartech.staff.service.MaterialService;
import ru.egartech.staff.service.MvcService;

import java.util.function.Function;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/materials")
public class MaterialMvcController {

    private static final String MODEL_ATTRIBUTE_MATERIAL = "material";
    private static final String REDIRECT_TO_MATERIALS = "redirect:/api/materials";
    private static final String REDIRECT_TO_MATERIALS_WITH_ID = "redirect:/api/materials/";

    private final MaterialService materialService;
    private final MvcService mvcService;

    @GetMapping
    public String getAllMaterials(
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(value = "sortType", required = false, defaultValue = "asc") String sortType,
            @RequestParam(value = "sortFieldName", required = false, defaultValue = "id") String sortFieldName,
            @RequestParam(value = "searchingFilter", required = false, defaultValue = "") String searchingFilter,
            Model model) {
        var materials = materialService.getAllMaterials(pageNumber, pageSize, sortType, sortFieldName, searchingFilter);
        model.addAttribute("materials", materials);
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("sortType", sortType);
        model.addAttribute("sortFieldName", sortFieldName);
        model.addAttribute("searchingFilter", searchingFilter);
        model.addAttribute("sortLink", (Function<String, String>) field ->
                mvcService.generateSortLink("materials", field, sortFieldName, sortType,
                        pageNumber, pageSize, searchingFilter));
        return "materials/list";
    }

    @GetMapping("/{materialId}")
    public String getMaterialById(@PathVariable("materialId") Long materialId, Model model) {
        MaterialInfoResponseDto material = materialService.getMaterialById(materialId);
        model.addAttribute(MODEL_ATTRIBUTE_MATERIAL, material);
        return "materials/info";
    }

    @GetMapping("/create")
    public String showCreateMaterialForm(Model model) {
        model.addAttribute(MODEL_ATTRIBUTE_MATERIAL, new MaterialSaveRequestDto());
        return "materials/create";
    }

    @PostMapping("/create")
    public String createMaterial(@ModelAttribute(MODEL_ATTRIBUTE_MATERIAL) @Valid MaterialSaveRequestDto materialSaveRequestDto,
                                 BindingResult result) {
        if (result.hasErrors()) {
            return "materials/create";
        }
        materialService.createMaterial(materialSaveRequestDto);
        return REDIRECT_TO_MATERIALS;
    }

    @GetMapping("/{materialId}/update")
    public String showEditMaterialForm(@PathVariable("materialId") Long materialId, Model model) {
        MaterialInfoResponseDto material = materialService.getMaterialById(materialId);
        model.addAttribute(MODEL_ATTRIBUTE_MATERIAL, material);
        return "materials/update";
    }

    @PutMapping("/{materialId}/update")
    public String updateMaterial(@PathVariable("materialId") Long materialId,
                                 @ModelAttribute(MODEL_ATTRIBUTE_MATERIAL) @Valid MaterialSaveRequestDto materialSaveRequestDto,
                                 BindingResult result) {
        if (result.hasErrors()) {
            return "materials/update";
        }
        materialService.updateMaterial(materialId, materialSaveRequestDto);
        return REDIRECT_TO_MATERIALS_WITH_ID + materialId;
    }

    @PostMapping("/{materialId}/delete")
    public String deleteMaterialById(@PathVariable("materialId") Long materialId) {
        materialService.deleteMaterialById(materialId);
        return REDIRECT_TO_MATERIALS;
    }
}