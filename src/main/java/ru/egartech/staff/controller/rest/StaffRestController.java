package ru.egartech.staff.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.egartech.staff.api.StaffApi;
import ru.egartech.staff.model.*;
import ru.egartech.staff.service.StaffService;

@RestController
@RequiredArgsConstructor
public class StaffRestController implements StaffApi {

    private final StaffService staffService;

    @Override
    public void deleteStaffById(Long staffId) {
        staffService.banStaffById(staffId);
    }

    @Override
    public StaffInfoPagingResponseDto getAllStaff(Integer pageNumber, Integer pageSize,
                                                  String sortType, String sortFieldName, String searchingFilter) {
        return staffService.getAllStaff(pageNumber, pageSize, sortType, sortFieldName, searchingFilter);
    }

    @Override
    public StaffAdminInfoResponseDto getStaffById(Long staffId) {
        return staffService.getStaffById(staffId);
    }

    @Override
    public StaffInfoResponseDto getStaffCardById(Long staffId) {
        return staffService.getStaffCardById(staffId);
    }

    @Override
    public void markAsUnbanned(Long staffId) {
        staffService.unbanStaffById(staffId);
    }

    @Override
    public void createStaff(StaffSaveRequestDto staffSaveRequestDto) {
        staffService.createStaff(staffSaveRequestDto);
    }

    @Override
    public void updateStaffCardById(Long staffId, StaffUpdateRequestDto staffUpdateRequestDto) {
        staffService.updateStaffCardById(staffId, staffUpdateRequestDto);
    }

    @Override
    public void updateStaffPositionById(Long staffId, StaffChangePositionRequestDto staffChangePositionRequestDto) {
        staffService.updateStaffPositionById(staffId, staffChangePositionRequestDto);
    }
}
