package ru.egartech.staff.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.egartech.staff.model.StaffPositionDto;

@Getter
@RequiredArgsConstructor
public enum Position {
    CONSTRUCTOR("Сборщик"),
    MANAGER("Менеджер"),
    DIRECTOR("Директор"),
    WAREHOUSEMAN("Кладовщик"),
    COURIER("Курьер"),
    PURCHASING_AGENT("Агент по закупкам");

    private final String value;

    public static Position toPositionEntity(StaffPositionDto positionDto){
        return switch (positionDto){
            case CONSTRUCTOR -> Position.CONSTRUCTOR;
            case MANAGER -> Position.MANAGER;
            case DIRECTOR -> Position.DIRECTOR;
            case WAREHOUSEMAN -> Position.WAREHOUSEMAN;
            case COURIER -> Position.COURIER;
            case PURCHASING_AGENT -> Position.PURCHASING_AGENT;
        };
    }

    public static StaffPositionDto toPositionDto(Position position){
        return switch (position){
            case CONSTRUCTOR -> StaffPositionDto.CONSTRUCTOR;
            case MANAGER -> StaffPositionDto.MANAGER;
            case DIRECTOR -> StaffPositionDto.DIRECTOR;
            case WAREHOUSEMAN -> StaffPositionDto.WAREHOUSEMAN;
            case COURIER -> StaffPositionDto.COURIER;
            case PURCHASING_AGENT -> StaffPositionDto.PURCHASING_AGENT;
        };
    }
}
