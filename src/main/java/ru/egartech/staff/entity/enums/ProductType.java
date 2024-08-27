package ru.egartech.staff.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.egartech.staff.model.ProductTypeDto;

@Getter
@RequiredArgsConstructor
public enum ProductType {
    SINGLE_SEAT("Одиночное сидение"),
    MULTIPLE_SEATS("Несколько сидений"),
    SLEEPING_OR_LYING("Для сна или лежачая"),
    ENTERTAINMENT("Развлечения"),
    TABLES("Столы"),
    STORAGE("Хранилища"),
    SETS("Наборы"),
    DECORATION("Декорации"),
    OTHER("Другое");

    private final String value;

    public static ProductTypeDto toProductDtoType(ProductType type){
        return switch (type) {
            case SINGLE_SEAT -> ProductTypeDto.SINGLE_SEAT;
            case MULTIPLE_SEATS -> ProductTypeDto.MULTIPLE_SEATS;
            case SLEEPING_OR_LYING -> ProductTypeDto.SLEEPING_OR_LYING;
            case ENTERTAINMENT -> ProductTypeDto.ENTERTAINMENT;
            case TABLES -> ProductTypeDto.TABLES;
            case STORAGE -> ProductTypeDto.STORAGE;
            case SETS -> ProductTypeDto.SETS;
            case DECORATION -> ProductTypeDto.DECORATION;
            default -> ProductTypeDto.OTHER;
        };
    }

    public static ProductType toProductEntityType(ProductTypeDto typeDto){
        return switch (typeDto) {
            case SINGLE_SEAT -> ProductType.SINGLE_SEAT;
            case MULTIPLE_SEATS -> ProductType.MULTIPLE_SEATS;
            case SLEEPING_OR_LYING -> ProductType.SLEEPING_OR_LYING;
            case ENTERTAINMENT -> ProductType.ENTERTAINMENT;
            case TABLES -> ProductType.TABLES;
            case STORAGE -> ProductType.STORAGE;
            case SETS -> ProductType.SETS;
            case DECORATION -> ProductType.DECORATION;
            default -> ProductType.OTHER;
        };
    }
}
