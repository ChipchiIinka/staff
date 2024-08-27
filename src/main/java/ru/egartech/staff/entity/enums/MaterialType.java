package ru.egartech.staff.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.egartech.staff.model.MaterialTypeDto;

@Getter
@RequiredArgsConstructor
public enum MaterialType {
    WOOD("Дерево"),
    BAMBOO("Бамбук"),
    METAL("Металл"),
    PLASTIC("Пластик"),
    GLASS("Стекло"),
    WOOL("Шерсть"),
    LEATHER("Кожа"),
    OTHER("Другое");

    private final String value;

    public static MaterialTypeDto toMaterialDtoType(MaterialType type){
        return switch (type){
            case WOOD -> MaterialTypeDto.WOOD;
            case BAMBOO -> MaterialTypeDto.BAMBOO;
            case METAL -> MaterialTypeDto.METAL;
            case PLASTIC -> MaterialTypeDto.PLASTIC;
            case GLASS -> MaterialTypeDto.GLASS;
            case WOOL -> MaterialTypeDto.WOOL;
            case LEATHER -> MaterialTypeDto.LEATHER;
            default -> MaterialTypeDto.OTHER;
        };
    }

    public static MaterialType toMaterialEntityType(MaterialTypeDto typeDto){
        return switch (typeDto){
            case WOOD -> MaterialType.WOOD;
            case BAMBOO -> MaterialType.BAMBOO;
            case METAL -> MaterialType.METAL;
            case PLASTIC -> MaterialType.PLASTIC;
            case GLASS -> MaterialType.GLASS;
            case WOOL -> MaterialType.WOOL;
            case LEATHER -> MaterialType.LEATHER;
            default -> MaterialType.OTHER;
        };
    }
}
