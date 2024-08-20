package ru.egartech.staff.service.mapper;

import org.mapstruct.Mapper;
import ru.egartech.staff.entity.StaffEntity;
import ru.egartech.staff.entity.enums.Position;
import ru.egartech.staff.entity.enums.Role;
import ru.egartech.staff.model.*;

import java.util.List;

@Mapper
public interface StaffMapper {

    List<StaffListInfoResponseDto> toListDto(List<StaffEntity> staff);

    //Только для List<StaffListInfoResponseDto>
    default StaffListInfoResponseDto toDto(StaffEntity staff) {
        return new StaffListInfoResponseDto()
                .fullName(staff.getFullName())
                .position(toPositionDto(staff.getPosition()));
    }

    default StaffAdminInfoResponseDto toAdminInfoDto(StaffEntity staff){
        return new StaffAdminInfoResponseDto()
                .id(staff.getId())
                .firstName(staff.getFullName().split("")[1])
                .secondName(staff.getFullName().split("")[0])
                .lastName(staff.getFullName().split("")[2])
                .login(staff.getLogin())
                .email(staff.getEmail())
                .isDeleted(staff.isDeleted())
                .role(toRoleDto(staff.getRole()))
                .position(toPositionDto(staff.getPosition()));
    }

    default StaffInfoResponseDto toInfoDto(StaffEntity staff){
        return new StaffInfoResponseDto()
                .firstName(staff.getFullName().split("")[1])
                .secondName(staff.getFullName().split("")[0])
                .lastName(staff.getFullName().split("")[2])
                .login(staff.getLogin())
                .email(staff.getEmail())
                .position(toPositionDto(staff.getPosition()));
    }

    default StaffEntity toEntity(StaffSaveRequestDto requestDto, StaffEntity staffEntity){
        staffEntity.setFullName(String.format("%s %s %s", requestDto.getSecondName(), requestDto.getFirstName(), requestDto.getLastName()));
        staffEntity.setLogin(requestDto.getLogin());
        staffEntity.setEmail(requestDto.getEmail());
        staffEntity.setPassword(requestDto.getPassword());
        staffEntity.setPhoneNumber(requestDto.getPhoneNumber());
        staffEntity.setRole(toRoleEntity(requestDto.getRole()));
        staffEntity.setPosition(toPositionEntity(requestDto.getPosition()));
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
        staffEntity.setRole(toRoleEntity(requestDto.getRole()));
        staffEntity.setPosition(toPositionEntity(requestDto.getPosition()));
        return staffEntity;
    }

    default Position toPositionEntity(StaffPositionDto positionDto){
        return switch (positionDto){
            case CONSTRUCTOR -> Position.CONSTRUCTOR;
            case MANAGER -> Position.MANAGER;
            case DIRECTOR -> Position.DIRECTOR;
            case WAREHOUSEMAN -> Position.WAREHOUSEMAN;
            case COURIER -> Position.COURIER;
            case PURCHASING_AGENT -> Position.PURCHASING_AGENT;
        };
    }

    default StaffPositionDto toPositionDto(Position position){
        return switch (position){
            case CONSTRUCTOR -> StaffPositionDto.CONSTRUCTOR;
            case MANAGER -> StaffPositionDto.MANAGER;
            case DIRECTOR -> StaffPositionDto.DIRECTOR;
            case WAREHOUSEMAN -> StaffPositionDto.WAREHOUSEMAN;
            case COURIER -> StaffPositionDto.COURIER;
            case PURCHASING_AGENT -> StaffPositionDto.PURCHASING_AGENT;
        };
    }

    default Role toRoleEntity(UserRoleDto roleDto){
        return switch (roleDto){
            case ADMIN -> Role.ADMIN;
            case USER -> Role.USER;
        };
    }

    default UserRoleDto toRoleDto(Role role){
        return switch (role){
            case ADMIN -> UserRoleDto.ADMIN;
            case USER -> UserRoleDto.USER;
        };
    }
}
