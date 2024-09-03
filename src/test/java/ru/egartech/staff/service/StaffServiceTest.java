package ru.egartech.staff.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.egartech.staff.entity.StaffEntity;
import ru.egartech.staff.model.*;
import ru.egartech.staff.repository.StaffRepository;
import ru.egartech.staff.service.mapper.StaffMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StaffServiceTest {

    @Mock
    StaffRepository staffRepository;

    @Mock
    StaffMapper staffMapper;

    @InjectMocks
    StaffService staffService;

    private Long staffId;
    private StaffEntity staffEntity;
    private StaffListInfoResponseDto staffListInfoResponseDto;
    private StaffAdminInfoResponseDto staffAdminInfoResponseDto;
    private StaffInfoResponseDto staffInfoResponseDto;
    private StaffSaveRequestDto staffSaveRequestDto;
    private StaffUpdateRequestDto staffUpdateRequestDto;
    private StaffChangePositionRequestDto staffChangePositionRequestDto;

    @BeforeEach
    void setUp() {
        staffId = 1L;
        staffEntity = new StaffEntity();
        staffListInfoResponseDto = new StaffListInfoResponseDto();
        staffAdminInfoResponseDto = new StaffAdminInfoResponseDto();
        staffInfoResponseDto = new StaffInfoResponseDto();
        staffSaveRequestDto = new StaffSaveRequestDto();
        staffUpdateRequestDto = new StaffUpdateRequestDto();
        staffChangePositionRequestDto = new StaffChangePositionRequestDto();
    }

    @Test
    void testGetAllStaff() {
        PageRequest pageRequest = PageRequest.of(1, 2, Sort.by(Sort.Direction.ASC, "id"));

        Page<StaffEntity> staffEntities = new PageImpl<>(List.of(staffEntity), pageRequest, 3);
        List<StaffListInfoResponseDto> staffDtos = List.of(staffListInfoResponseDto);

        when(staffRepository.findAll(pageRequest)).thenReturn(staffEntities);
        when(staffMapper.toListDto(staffEntities)).thenReturn(staffDtos);

        StaffInfoPagingResponseDto response = staffService.getAllStaff(1, 2, "asc", "id");

        assertEquals(2, response.getPaging().getPages());
        assertEquals(3, response.getPaging().getCount());
        assertEquals(1, response.getPaging().getPageNumber());
        assertEquals(2, response.getPaging().getPageSize());
        assertEquals(staffDtos, response.getContent());
    }

    @Test
    void testGetStaffById() {
        staffEntity.setId(staffId);
        staffEntity.setFullName("Ivan Ivanov Ivanovich");

        when(staffRepository.findById(staffId)).thenReturn(Optional.of(staffEntity));
        when(staffMapper.toAdminInfoDto(staffEntity)).thenReturn(staffAdminInfoResponseDto);

        StaffAdminInfoResponseDto actualDto = staffService.getStaffById(staffId);

        assertEquals(staffAdminInfoResponseDto, actualDto);
    }

    @Test
    void testGetStaffCardById() {
        staffEntity.setId(staffId);
        staffEntity.setFullName("Ivan Ivanov Ivanovich");

        when(staffRepository.findById(staffId)).thenReturn(Optional.of(staffEntity));
        when(staffMapper.toInfoDto(staffEntity)).thenReturn(staffInfoResponseDto);

        StaffInfoResponseDto actualDto = staffService.getStaffCardById(staffId);

        assertEquals(staffInfoResponseDto, actualDto);
    }

    @Test
    void testSaveStaff() {
        when(staffMapper.toEntity(staffSaveRequestDto, staffEntity)).thenReturn(staffEntity);

        staffService.createStaff(staffSaveRequestDto);

        verify(staffRepository, times(1)).save(staffEntity);
    }

    @Test
    void testUpdateStaffCardById() {
        when(staffRepository.findById(staffId)).thenReturn(Optional.of(staffEntity));
        when(staffMapper.toCardUpdate(staffUpdateRequestDto, staffEntity)).thenReturn(staffEntity);

        staffService.updateStaffCardById(staffId, staffUpdateRequestDto);

        verify(staffRepository, times(1)).save(staffEntity);
    }

    @Test
    void testUpdateStaffPositionById() {
        when(staffRepository.findById(staffId)).thenReturn(Optional.of(staffEntity));
        when(staffMapper.toPositionChange(staffChangePositionRequestDto, staffEntity)).thenReturn(staffEntity);

        staffService.updateStaffPositionById(staffId, staffChangePositionRequestDto);

        verify(staffRepository, times(1)).save(staffEntity);
    }

    @Test
    void testBanStaffById() {
        staffService.banStaffById(staffId);
        verify(staffRepository, times(1)).markAsBanned(staffId);
    }

    @Test
    void testUnbanStaffById() {
        staffService.unbanStaffById(staffId);
        verify(staffRepository, times(1)).markAsUnbanned(staffId);
    }
}