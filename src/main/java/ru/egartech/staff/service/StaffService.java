package ru.egartech.staff.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.egartech.staff.cache.Caches;
import ru.egartech.staff.entity.StaffEntity;
import ru.egartech.staff.exception.ErrorType;
import ru.egartech.staff.exception.StaffException;
import ru.egartech.staff.model.*;
import ru.egartech.staff.repository.StaffRepository;
import ru.egartech.staff.service.mapper.StaffMapper;

@Service
@RequiredArgsConstructor
public class StaffService {

    private final StaffRepository staffRepository;
    private final StaffMapper staffMapper;

    @Cacheable(Caches.STAFF_CACHE)
    public StaffInfoPagingResponseDto getAllStaff(Integer pageNo, Integer pageSize,
                                                  String sortType, String sortFieldName) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortType), sortFieldName);
        PagingDto paging = new PagingDto();
        Page<StaffEntity> staffEntities = staffRepository.findAll(
                PageRequest.of(pageNo, pageSize, sort));
        paging.setPageNumber(pageNo);
        paging.setPageSize(pageSize);
        paging.setCount(staffEntities.getTotalElements());
        paging.setPages(staffEntities.getTotalPages());
        return new StaffInfoPagingResponseDto()
                .paging(paging)
                .content(staffMapper.toListDto(staffEntities));
    }

    @Cacheable(value = Caches.STAFF_CACHE, key = "'info:' + #staffId")
    public StaffAdminInfoResponseDto getStaffById(Long staffId) {
        return staffMapper.toAdminInfoDto(staffRepository.findById(staffId)
                .orElseThrow(() -> new StaffException(ErrorType.NOT_FOUND, "Сотрудника с таким id не существует")));
    }

    @Cacheable(value = Caches.STAFF_CACHE, key = "'card:' + #staffId")
    public StaffInfoResponseDto getStaffCardById(Long staffId) {
        return staffMapper.toInfoDto(staffRepository.findById(staffId)
                .orElseThrow(() -> new StaffException(ErrorType.NOT_FOUND, "Сотрудника с таким id не существует")));
    }

    @CacheEvict(value = Caches.STAFF_CACHE, allEntries = true)
    public void createStaff(StaffSaveRequestDto staffSaveRequestDto) {
        StaffEntity staffEntity = new StaffEntity();
        staffRepository.save(staffMapper.toEntity(staffSaveRequestDto, staffEntity));
    }

    @CacheEvict(value = Caches.STAFF_CACHE, allEntries = true)
    public void updateStaffCardById(Long staffId, StaffUpdateRequestDto staffUpdateRequestDto) {
        StaffEntity staffEntity = staffRepository.findById(staffId)
                .orElseThrow(() -> new StaffException(ErrorType.NOT_FOUND, "Сотрудник не найден"));
        staffRepository.save(staffMapper.toCardUpdate(staffUpdateRequestDto, staffEntity));
    }

    @CacheEvict(value = Caches.STAFF_CACHE, allEntries = true)
    public void updateStaffPositionById(Long staffId, StaffChangePositionRequestDto staffChangePositionRequestDto) {
        StaffEntity staffEntity = staffRepository.findById(staffId)
                .orElseThrow(() -> new StaffException(ErrorType.NOT_FOUND, "Сотрудник не найден"));
        staffRepository.save(staffMapper.toPositionChange(staffChangePositionRequestDto, staffEntity));
    }

    @Transactional
    @CacheEvict(value = Caches.STAFF_CACHE, allEntries = true)
    public void banStaffById(Long staffId) {
        staffRepository.markAsBanned(staffId);
    }

    @Transactional
    @CacheEvict(value = Caches.STAFF_CACHE, allEntries = true)
    public void unbanStaffById(Long staffId) {
        staffRepository.markAsUnbanned(staffId);
    }
}
