package ru.egartech.staff.controller.mvc;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.egartech.staff.model.*;
import ru.egartech.staff.service.StaffService;

import java.util.function.Function;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/staff")
public class StaffMvcController {

    private static final String MODEL_ATTRIBUTE_STAFF = "staff";
    private static final String REDIRECT_TO_STAFF = "redirect:/api/staff";
    private static final String REDIRECT_TO_STAFF_WITH_ID = "redirect:/api/staff/";

    private final StaffService staffService;

    @GetMapping
    public String getAllStaff(
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(value = "sortType", required = false, defaultValue = "asc") String sortType,
            @RequestParam(value = "sortFieldName", required = false, defaultValue = "id") String sortFieldName,
            @RequestParam(value = "searchingFilter", required = false, defaultValue = "false") String searchingFilter,
            Model model) {
        var response = staffService.getAllStaff(pageNumber, pageSize, sortType, sortFieldName, searchingFilter);
        model.addAttribute("staff_list", response);
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("sortType", sortType);
        model.addAttribute("sortFieldName", sortFieldName);
        model.addAttribute("searchingFilter", searchingFilter);
        model.addAttribute("sortLink", (Function<String, String>) field ->
                staffService.generateSortLink(field, sortFieldName, sortType, pageNumber, pageSize, searchingFilter));
        return "staff/list";
    }

    @GetMapping("/{staffId}/info")
    public String getStaffById(@PathVariable Long staffId, Model model) {
        StaffAdminInfoResponseDto response = staffService.getStaffById(staffId);
        model.addAttribute(MODEL_ATTRIBUTE_STAFF, response);
        return "staff/info";
    }

    @GetMapping("/{staffId}/card")
    public String getStaffCardById(@PathVariable Long staffId, Model model) {
        StaffInfoResponseDto response = staffService.getStaffCardById(staffId);
        model.addAttribute(MODEL_ATTRIBUTE_STAFF, response);
        return "staff/card";
    }

    @GetMapping("/create")
    public String showCreateStaffForm(Model model) {
        model.addAttribute(MODEL_ATTRIBUTE_STAFF, new StaffSaveRequestDto());
        return "staff/create";
    }

    @PostMapping("/create")
    public String createStaff(@ModelAttribute(MODEL_ATTRIBUTE_STAFF) @Valid StaffSaveRequestDto staffSaveRequestDto,
                            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "staff/create";
        }
        staffService.createStaff(staffSaveRequestDto);
        return REDIRECT_TO_STAFF;
    }


    @GetMapping("/{staffId}/update/card")
    public String showUpdateStaffCardForm(@PathVariable Long staffId, Model model) {
        model.addAttribute(MODEL_ATTRIBUTE_STAFF, staffService.getStaffById(staffId));
        return "/staff/update-card";
    }

    @PatchMapping("/{staffId}/update/card")
    public String updateStaffCardById(@PathVariable Long staffId,
                                      @ModelAttribute(MODEL_ATTRIBUTE_STAFF) @Valid StaffUpdateRequestDto staffUpdateRequestDto,
                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "/staff/update-card";
        }
        staffService.updateStaffCardById(staffId, staffUpdateRequestDto);
        return  REDIRECT_TO_STAFF_WITH_ID + staffId + "/card";
    }


    @GetMapping("/{staffId}/update/position")
    public String showUpdateStaffPositionForm(@PathVariable Long staffId, Model model) {
        model.addAttribute(MODEL_ATTRIBUTE_STAFF, staffService.getStaffById(staffId));
        return "staff/update-position";
    }

    @PatchMapping("/{staffId}/update/position")
    public String updateStaffPositionById(@PathVariable Long staffId,
                                          @ModelAttribute(MODEL_ATTRIBUTE_STAFF) @Valid StaffChangePositionRequestDto staffChangePositionRequestDto,
                                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "staff/update-position";
        }
        staffService.updateStaffPositionById(staffId, staffChangePositionRequestDto);
        return REDIRECT_TO_STAFF_WITH_ID + staffId + "/info";
    }

    @DeleteMapping("/{staffId}/ban")
    public String banStaffById(@PathVariable Long staffId) {
        staffService.banStaffById(staffId);
        return REDIRECT_TO_STAFF;
    }

    @PatchMapping("/{staffId}/unban")
    public String unbanStaffById(@PathVariable Long staffId) {
        staffService.unbanStaffById(staffId);
        return REDIRECT_TO_STAFF_WITH_ID + staffId + "/info";
    }
}
