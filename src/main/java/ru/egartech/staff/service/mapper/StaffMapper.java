package ru.egartech.staff.service.mapper;

import org.mapstruct.*;
import org.springframework.data.domain.Page;
import ru.egartech.staff.entity.StaffEntity;
import ru.egartech.staff.entity.enums.Position;
import ru.egartech.staff.entity.enums.Role;
import ru.egartech.staff.model.*;

import java.util.List;

@Mapper(componentModel="spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StaffMapper {

    List<StaffListInfoResponseDto> toListDto(Page<StaffEntity> staff);

    //Только для List<StaffListInfoResponseDto>
    @Mapping(target = "position", expression = "java(mapPosition(staff.getPosition()))")
    StaffListInfoResponseDto toDto(StaffEntity staff);

    @Mapping(target = "secondName", expression = "java(staff.getFullName().split(\" \")[0])")
    @Mapping(target = "firstName", expression = "java(staff.getFullName().split(\" \")[1])")
    @Mapping(target = "lastName", expression = "java(staff.getFullName().split(\" \")[2])")
    @Mapping(target = "role", expression = "java(mapRole(staff.getRole()))")
    @Mapping(target = "position", expression = "java(mapPosition(staff.getPosition()))")
    @Mapping(target = "isDeleted", source = "deleted")
    StaffAdminInfoResponseDto toAdminInfoDto(StaffEntity staff);

    @Mapping(target = "secondName", expression = "java(staff.getFullName().split(\" \")[0])")
    @Mapping(target = "firstName", expression = "java(staff.getFullName().split(\" \")[1])")
    @Mapping(target = "lastName", expression = "java(staff.getFullName().split(\" \")[2])")
    @Mapping(target = "position", expression = "java(mapPosition(staff.getPosition()))")
    StaffInfoResponseDto toInfoDto(StaffEntity staff);

    @Mapping(target = "fullName", expression = "java(toFullName(requestDto))")
    @Mapping(target = "role", expression = "java(mapRole(requestDto.getRole()))")
    @Mapping(target = "position", expression = "java(mapPosition(requestDto.getPosition()))")
    StaffEntity toEntity(StaffSaveRequestDto requestDto, @MappingTarget StaffEntity staffEntity);

    @Mapping(target = "fullName", expression = "java(toFullName(requestDto))")
    StaffEntity toCardUpdate(StaffUpdateRequestDto requestDto, @MappingTarget StaffEntity staffEntity);

    @Mapping(target = "role", expression = "java(mapRole(requestDto.getRole()))")
    @Mapping(target = "position", expression = "java(mapPosition(requestDto.getPosition()))")
    StaffEntity toPositionChange(StaffChangePositionRequestDto requestDto, @MappingTarget StaffEntity staffEntity);

    default String toFullName(StaffSaveRequestDto requestDto){
        return String.format("%s %s %s", requestDto.getSecondName(), requestDto.getFirstName(), requestDto.getLastName());
    }

    default String toFullName(StaffUpdateRequestDto requestDto){
        return String.format("%s %s %s", requestDto.getSecondName(), requestDto.getFirstName(), requestDto.getLastName());
    }

    default StaffPositionDto mapPosition(Position position) {
        return Position.toPositionDto(position);
    }

    default Position mapPosition(StaffPositionDto position) {
        return Position.toPositionEntity(position);
    }

    default UserRoleDto mapRole(Role role) {
        return Role.toRoleDto(role);
    }

    default Role mapRole(UserRoleDto role) {
        return Role.toRoleEntity(role);
    }
}
