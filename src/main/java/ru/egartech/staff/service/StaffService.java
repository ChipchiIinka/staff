package ru.egartech.staff.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.egartech.staff.cache.Caches;
import ru.egartech.staff.entity.StaffEntity;
import ru.egartech.staff.exception.ErrorType;
import ru.egartech.staff.exception.StaffException;
import ru.egartech.staff.model.*;
import ru.egartech.staff.repository.StaffRepository;
import ru.egartech.staff.repository.specification.StaffSpecification;
import ru.egartech.staff.service.mapper.StaffMapper;

@Service
@RequiredArgsConstructor
public class StaffService {

    private final StaffRepository staffRepository;
    private final StaffMapper staffMapper;

    @Cacheable(Caches.STAFF_CACHE)
    public StaffInfoPagingResponseDto getAllStaff(Integer pageNo, Integer pageSize,
                                                  String sortType, String sortFieldName, String searchingFilter) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortType), sortFieldName);
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, sort);
        Specification<StaffEntity> staffSpecification = Specification
                .where(StaffSpecification.hasLogin(searchingFilter))
                .or(StaffSpecification.hasFullName(searchingFilter))
                .or(StaffSpecification.hasPosition(searchingFilter))
                .or(StaffSpecification.hasDeleted(searchingFilter));
        Page<StaffEntity> staffEntities = staffRepository.findAll(staffSpecification, pageRequest);
        PagingDto paging = new PagingDto()
                .pageNumber(pageNo)
                .pageSize(pageSize)
                .count(staffEntities.getTotalElements())
                .pages(staffEntities.getTotalPages());
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

    public String generateSortLink(String field, String currentSortField, String currentSortType, int pageNumber,
                                   int pageSize, String searchingFilter) {
        String newSortType = "asc".equals(currentSortType) && field.equals(currentSortField) ? "desc" : "asc";
        return String.format("/api/staff?pageNumber=%d&pageSize=%d&sortFieldName=%s&sortType=%s&searchingFilter=%s",
                pageNumber, pageSize, field, newSortType, searchingFilter);
    }
}
