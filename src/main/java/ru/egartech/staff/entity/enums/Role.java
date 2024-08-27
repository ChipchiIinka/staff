package ru.egartech.staff.entity.enums;

import ru.egartech.staff.model.UserRoleDto;

public enum Role {
    USER,
    ADMIN;

    public static Role toRoleEntity(UserRoleDto roleDto){
        return switch (roleDto){
            case ADMIN -> Role.ADMIN;
            case USER -> Role.USER;
        };
    }

    public static UserRoleDto toRoleDto(Role role){
        return switch (role){
            case ADMIN -> UserRoleDto.ADMIN;
            case USER -> UserRoleDto.USER;
        };
    }
}
