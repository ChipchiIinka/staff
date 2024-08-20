package ru.egartech.staff.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.egartech.staff.entity.StaffEntity;
import ru.egartech.staff.model.*;
import ru.egartech.staff.repository.StaffRepository;
import ru.egartech.staff.service.mapper.StaffMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffService {

    private final StaffRepository staffRepository;
    private final StaffMapper staffMapper;

    public List<StaffListInfoResponseDto> getAllStaff() {
        return staffMapper.toListDto(staffRepository.findAll());
    }

    public StaffAdminInfoResponseDto getStaffById(Long staffId) {
        return staffMapper.toAdminInfoDto(staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"))); //TODO временная заглушка, поменять на обработчик
    }

    public StaffInfoResponseDto getStaffCardById(Long staffId) {
        return staffMapper.toInfoDto(staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"))); //TODO временная заглушка, поменять на обработчик
    }

    public void saveStaff(StaffSaveRequestDto staffSaveRequestDto) {
        StaffEntity staffEntity = new StaffEntity();
        staffRepository.save(staffMapper.toEntity(staffSaveRequestDto, staffEntity));
    }

    public void updateStaffCardById(Long staffId, StaffUpdateRequestDto staffUpdateRequestDto) {
        StaffEntity staffEntity = staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found")); //TODO временная заглушка, поменять на обработчик
        staffRepository.save(staffMapper.toCardUpdate(staffUpdateRequestDto, staffEntity));
    }

    public void updateStaffPositionById(Long staffId, StaffChangePositionRequestDto staffChangePositionRequestDto) {
        StaffEntity staffEntity = staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found")); //TODO временная заглушка, поменять на обработчик
        staffRepository.save(staffMapper.toPositionChange(staffChangePositionRequestDto, staffEntity));
    }

    public void banStaffById(Long staffId) {
        staffRepository.markAsBanned(staffId);
    }

    public void unbanStaffById(Long staffId) {
        staffRepository.markAsUnbanned(staffId);
    }

}
