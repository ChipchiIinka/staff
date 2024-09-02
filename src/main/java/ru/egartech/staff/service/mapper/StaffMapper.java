package ru.egartech.staff.service.mapper;

import org.mapstruct.*;
import org.springframework.data.domain.Page;
import ru.egartech.staff.entity.StaffEntity;
import ru.egartech.staff.model.*;

import java.util.List;

@Mapper(componentModel="spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StaffMapper {

    String DEFAULT_STAFF_ENUMS_PACKAGE = "ru.egartech.staff.entity.enums.";

    List<StaffListInfoResponseDto> toListDto(Page<StaffEntity> staff);

    //Только для List<StaffListInfoResponseDto>
    @Mapping(target = "position", expression = "java(" + DEFAULT_STAFF_ENUMS_PACKAGE +
            "Position.toPositionDto(staff.getPosition()))")
    StaffListInfoResponseDto toDto(StaffEntity staff);

    @Mapping(target = "secondName", expression = "java(staff.getFullName().split(\" \")[0])")
    @Mapping(target = "firstName", expression = "java(staff.getFullName().split(\" \")[1])")
    @Mapping(target = "lastName", expression = "java(staff.getFullName().split(\" \")[2])")
    @Mapping(target = "role", expression = "java(" + DEFAULT_STAFF_ENUMS_PACKAGE +
            "Role.toRoleDto(staff.getRole()))")
    @Mapping(target = "position", expression = "java(" + DEFAULT_STAFF_ENUMS_PACKAGE +
            "Position.toPositionDto(staff.getPosition()))")
    @Mapping(target = "isDeleted", source = "deleted")
    StaffAdminInfoResponseDto toAdminInfoDto(StaffEntity staff);

    @Mapping(target = "secondName", expression = "java(staff.getFullName().split(\" \")[0])")
    @Mapping(target = "firstName", expression = "java(staff.getFullName().split(\" \")[1])")
    @Mapping(target = "lastName", expression = "java(staff.getFullName().split(\" \")[2])")
    @Mapping(target = "position", expression = "java(" + DEFAULT_STAFF_ENUMS_PACKAGE +
            "Position.toPositionDto(staff.getPosition()))")
    StaffInfoResponseDto toInfoDto(StaffEntity staff);

    @Mapping(target = "fullName",
            expression = "java(toFullName(requestDto.getSecondName(), requestDto.getFirstName(), requestDto.getLastName()))")
    @Mapping(target = "role", expression = "java(" + DEFAULT_STAFF_ENUMS_PACKAGE +
            "Role.toRoleEntity(requestDto.getRole()))")
    @Mapping(target = "position", expression = "java(" + DEFAULT_STAFF_ENUMS_PACKAGE +
            "Position.toPositionEntity(requestDto.getPosition()))")
    @Mapping(target = "deleted", expression = "java(false)")
    StaffEntity toEntity(StaffSaveRequestDto requestDto, @MappingTarget StaffEntity staffEntity);

    @Mapping(target = "fullName",
            expression = "java(toFullName(requestDto.getSecondName(), requestDto.getFirstName(), requestDto.getLastName()))")
    StaffEntity toCardUpdate(StaffUpdateRequestDto requestDto, @MappingTarget StaffEntity staffEntity);

    @Mapping(target = "role", expression = "java(" + DEFAULT_STAFF_ENUMS_PACKAGE +
            "Role.toRoleEntity(requestDto.getRole()))")
    @Mapping(target = "position", expression = "java(" + DEFAULT_STAFF_ENUMS_PACKAGE +
            "Position.toPositionEntity(requestDto.getPosition()))")
    StaffEntity toPositionChange(StaffChangePositionRequestDto requestDto, @MappingTarget StaffEntity staffEntity);

    default String toFullName(String secondName, String firstName, String lastName){
        return String.format("%s %s %s", secondName, firstName, lastName);
    }
}
