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

    /**
     * Получение списка всех сотрудников с постраничной навигацией, фильтрацией и сортировкой
     *
     * @param pageNo          Номер страницы
     * @param pageSize        Размер страницы
     * @param sortType        Тип сортировки (asc/desc)
     * @param sortFieldName   Поле для сортировки
     * @param searchingFilter Фильтр для поиска по логину, ФИО, должности или статусу удаления
     * @return Страница сотрудников с информацией о постраничной навигации
     */
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

    /**
     * Получение информации о сотруднике по его идентификатору (для административных целей)
     *
     * @param staffId Идентификатор сотрудника
     * @return Административная информация о сотруднике
     */
    @Cacheable(value = Caches.STAFF_CACHE, key = "'info:' + #staffId")
    public StaffAdminInfoResponseDto getStaffById(Long staffId) {
        return staffMapper.toAdminInfoDto(staffRepository.findById(staffId)
                .orElseThrow(() -> new StaffException(ErrorType.NOT_FOUND, "Сотрудника с таким id не существует")));
    }

    /**
     * Получение карточки сотрудника по его идентификатору
     *
     * @param staffId Идентификатор сотрудника
     * @return Карточка с основной информацией о сотруднике
     */
    @Cacheable(value = Caches.STAFF_CACHE, key = "'card:' + #staffId")
    public StaffInfoResponseDto getStaffCardById(Long staffId) {
        return staffMapper.toInfoDto(staffRepository.findById(staffId)
                .orElseThrow(() -> new StaffException(ErrorType.NOT_FOUND, "Сотрудника с таким id не существует")));
    }

    /**
     * Создание нового сотрудника
     *
     * @param staffSaveRequestDto Данные для создания нового сотрудника
     */
    @CacheEvict(value = Caches.STAFF_CACHE, allEntries = true)
    public void createStaff(StaffSaveRequestDto staffSaveRequestDto) {
        StaffEntity staffEntity = new StaffEntity();
        staffRepository.save(staffMapper.toEntity(staffSaveRequestDto, staffEntity));
    }

    /**
     * Обновление информации в карточке сотрудника(публичная информация) по его идентификатору
     *
     * @param staffId                Идентификатор сотрудника
     * @param staffUpdateRequestDto  DTO с обновленной информацией сотрудника(ФИО)
     */
    @CacheEvict(value = Caches.STAFF_CACHE, allEntries = true)
    public void updateStaffCardById(Long staffId, StaffUpdateRequestDto staffUpdateRequestDto) {
        StaffEntity staffEntity = staffRepository.findById(staffId)
                .orElseThrow(() -> new StaffException(ErrorType.NOT_FOUND, "Сотрудник не найден"));
        staffRepository.save(staffMapper.toCardUpdate(staffUpdateRequestDto, staffEntity));
    }

    /**
     * Изменение должности сотрудника по его идентификатору
     *
     * @param staffId                    Идентификатор сотрудника
     * @param staffChangePositionRequestDto DTO с новой должностью сотрудника
     */
    @CacheEvict(value = Caches.STAFF_CACHE, allEntries = true)
    public void updateStaffPositionById(Long staffId, StaffChangePositionRequestDto staffChangePositionRequestDto) {
        StaffEntity staffEntity = staffRepository.findById(staffId)
                .orElseThrow(() -> new StaffException(ErrorType.NOT_FOUND, "Сотрудник не найден"));
        staffRepository.save(staffMapper.toPositionChange(staffChangePositionRequestDto, staffEntity));
    }

    /**
     * Блокировка сотрудника по его идентификатору
     *
     * @param staffId Идентификатор сотрудника
     */
    @Transactional
    @CacheEvict(value = Caches.STAFF_CACHE, allEntries = true)
    public void banStaffById(Long staffId) {
        staffRepository.markAsBanned(staffId);
    }

    /**
     * Разблокировка сотрудника по его идентификатору
     *
     * @param staffId Идентификатор сотрудника
     */
    @Transactional
    @CacheEvict(value = Caches.STAFF_CACHE, allEntries = true)
    public void unbanStaffById(Long staffId) {
        staffRepository.markAsUnbanned(staffId);
    }
}
