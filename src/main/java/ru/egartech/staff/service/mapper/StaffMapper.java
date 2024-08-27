package ru.egartech.staff.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.Page;
import ru.egartech.staff.entity.StaffEntity;
import ru.egartech.staff.entity.enums.Position;
import ru.egartech.staff.entity.enums.Role;
import ru.egartech.staff.model.*;

import java.util.List;

@Mapper(componentModel="spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface StaffMapper {

    List<StaffListInfoResponseDto> toListDto(Page<StaffEntity> staff);

    //Только для List<StaffListInfoResponseDto>
    default StaffListInfoResponseDto toDto(StaffEntity staff) {
        return new StaffListInfoResponseDto()
                .fullName(staff.getFullName())
                .position(Position.toPositionDto(staff.getPosition()));
    }

    default StaffAdminInfoResponseDto toAdminInfoDto(StaffEntity staff){
        return new StaffAdminInfoResponseDto()
                .id(staff.getId())
                .firstName(staff.getFullName().split(" ")[1])
                .secondName(staff.getFullName().split(" ")[0])
                .lastName(staff.getFullName().split(" ")[2])
                .login(staff.getLogin())
                .email(staff.getEmail())
                .isDeleted(staff.isDeleted())
                .role(Role.toRoleDto(staff.getRole()))
                .position(Position.toPositionDto(staff.getPosition()));
    }

    default StaffInfoResponseDto toInfoDto(StaffEntity staff){
        return new StaffInfoResponseDto()
                .firstName(staff.getFullName().split(" ")[1])
                .secondName(staff.getFullName().split(" ")[0])
                .lastName(staff.getFullName().split(" ")[2])
                .login(staff.getLogin())
                .email(staff.getEmail())
                .position(Position.toPositionDto(staff.getPosition()));
    }

    default StaffEntity toEntity(StaffSaveRequestDto requestDto, StaffEntity staffEntity){
        staffEntity.setFullName(String.format("%s %s %s", requestDto.getSecondName(), requestDto.getFirstName(), requestDto.getLastName()));
        staffEntity.setLogin(requestDto.getLogin());
        staffEntity.setEmail(requestDto.getEmail());
        staffEntity.setPassword(requestDto.getPassword());
        staffEntity.setPhoneNumber(requestDto.getPhoneNumber());
        staffEntity.setRole(Role.toRoleEntity(requestDto.getRole()));
        staffEntity.setPosition(Position.toPositionEntity(requestDto.getPosition()));
        staffEntity.setDeleted(false);
        return staffEntity;
    }

    default StaffEntity toCardUpdate(StaffUpdateRequestDto requestDto, StaffEntity staffEntity){
        staffEntity.setFullName(String.format("%s %s %s",
                requestDto.getSecondName(), requestDto.getFirstName(), requestDto.getLastName()));
        staffEntity.setPassword(requestDto.getPassword());
        return staffEntity;
    }

    default StaffEntity toPositionChange(StaffChangePositionRequestDto requestDto, StaffEntity staffEntity){
        staffEntity.setRole(Role.toRoleEntity(requestDto.getRole()));
        staffEntity.setPosition(Position.toPositionEntity(requestDto.getPosition()));
        return staffEntity;
    }
}
