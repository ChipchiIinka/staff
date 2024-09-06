package ru.egartech.staff.service.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.egartech.staff.entity.StaffEntity;
import ru.egartech.staff.entity.enums.Position;
import ru.egartech.staff.entity.enums.Role;
import ru.egartech.staff.model.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = StaffMapperImpl.class)
class StaffMapperTest {

    @Autowired
    private StaffMapper staffMapper;

    @Test
    void testToListDto() {
        StaffEntity staffEntity1 = createStaffEntity(1L);
        StaffEntity staffEntity2 = createStaffEntity(2L);

        List<StaffEntity> staffList = List.of(staffEntity1, staffEntity2);
        Page<StaffEntity> productPage = new PageImpl<>(staffList, PageRequest.of(0, 10), staffList.size());

        List<StaffListInfoResponseDto> result = staffMapper.toListDto(productPage);

        assertEquals(2, result.size());
        assertEquals("Фамилия Имя Отчество", result.get(0).getFullName());
        assertEquals("Фамилия Имя Отчество", result.get(1).getFullName());
        assertEquals(StaffPositionDto.DIRECTOR, result.get(0).getPosition());
        assertEquals(StaffPositionDto.DIRECTOR, result.get(1).getPosition());
    }

    @Test
    void testToDto() {
        StaffEntity staffEntity = createStaffEntity(1L);

        StaffListInfoResponseDto result = staffMapper.toDto(staffEntity);

        assertNotNull(result);
        assertEquals("Фамилия Имя Отчество", result.getFullName());
        assertEquals(StaffPositionDto.DIRECTOR, result.getPosition());
    }

    @Test
    void testToAdminInfoDto() {
        StaffEntity staffEntity = createStaffEntity(1L);

        StaffAdminInfoResponseDto result = staffMapper.toAdminInfoDto(staffEntity);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Имя", result.getFirstName());
        assertEquals("Фамилия", result.getSecondName());
        assertEquals("Отчество", result.getLastName());
        assertEquals("login1", result.getLogin());
        assertEquals("email1@example.com", result.getEmail());
        assertEquals(UserRoleDto.ADMIN, result.getRole());
        assertEquals(StaffPositionDto.DIRECTOR, result.getPosition());
        assertEquals(false, result.getIsDeleted());
    }

    @Test
    void testToInfoDto() {
        StaffEntity staffEntity = createStaffEntity(1L);

        StaffInfoResponseDto result = staffMapper.toInfoDto(staffEntity);

        assertNotNull(result);
        assertEquals("Имя", result.getFirstName());
        assertEquals("Фамилия", result.getSecondName());
        assertEquals("Отчество", result.getLastName());
        assertEquals("login1", result.getLogin());
        assertEquals("email1@example.com", result.getEmail());
        assertEquals(StaffPositionDto.DIRECTOR, result.getPosition());
    }

    @Test
    void testToEntity() {
        StaffSaveRequestDto requestDto = new StaffSaveRequestDto()
                .login("login1")
                .firstName("ИмяДВА")
                .secondName("ФамилияДВА")
                .lastName("ОтчествоДВА")
                .password("123456")
                .email("emai@example.com")
                .position(StaffPositionDto.COURIER)
                .role(UserRoleDto.USER)
                .phoneNumber("+129032342122");
        StaffEntity result = new StaffEntity();
        result.setId(1L);

        staffMapper.toEntity(requestDto, result);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("login1", result.getLogin());
        assertEquals("ФамилияДВА ИмяДВА ОтчествоДВА", result.getFullName());
        assertEquals("123456", result.getPassword());
        assertEquals("login1", result.getLogin());
        assertEquals("emai@example.com", result.getEmail());
        assertEquals(Position.COURIER, result.getPosition());
        assertEquals(Role.USER, result.getRole());
        assertEquals("+129032342122", result.getPhoneNumber());
        assertFalse(result.isDeleted());
    }

    @Test
    void testToCardUpdate() {
        StaffUpdateRequestDto requestDto = new StaffUpdateRequestDto()
                .firstName("ИмяДВА")
                .secondName("ФамилияДВА")
                .lastName("ОтчествоДВА");

        StaffEntity staffEntity = createStaffEntity(1L);
        staffMapper.toCardUpdate(requestDto, staffEntity);

        assertNotNull(staffEntity);
        assertEquals("ФамилияДВА ИмяДВА ОтчествоДВА", staffEntity.getFullName());
    }

    @Test
    void testToPositionChange() {
        StaffEntity staffEntity = createStaffEntity(1L);
        StaffChangePositionRequestDto staffChangePositionRequestDto = new StaffChangePositionRequestDto()
                .position(StaffPositionDto.COURIER)
                .role(UserRoleDto.USER);

        staffMapper.toPositionChange(staffChangePositionRequestDto, staffEntity);

        assertNotNull(staffEntity);
        assertEquals(Role.USER, staffEntity.getRole());
        assertEquals(Position.COURIER, staffEntity.getPosition());
    }

    private StaffEntity createStaffEntity(Long staffId) {
        StaffEntity staffEntity = new StaffEntity();
        staffEntity.setId(staffId);
        staffEntity.setLogin("login" + staffId);
        staffEntity.setFullName("Фамилия Имя Отчество");
        staffEntity.setPassword("password" + staffId);
        staffEntity.setPhoneNumber("+12903234212" + staffId);
        staffEntity.setEmail(String.format("email%d@example.com", staffId));
        staffEntity.setRole(Role.ADMIN);
        staffEntity.setPosition(Position.DIRECTOR);
        staffEntity.setDeleted(false);
        return staffEntity;
    }
}