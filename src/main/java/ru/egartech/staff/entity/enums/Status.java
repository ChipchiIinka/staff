package ru.egartech.staff.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.egartech.staff.entity.OrderEntity;
import ru.egartech.staff.model.OrderStatusDto;

@Getter
@RequiredArgsConstructor
public enum Status {
    COMPLETED("Завершен"),
    DELIVERY("Доставка"),
    PACKAGING("Упаковка"),
    ASSEMBLY("Сборка"),
    PREPARATION("Подготовка"),
    ACCEPTED("Принят"),
    CANCELED("Отменен"),
    ACCEPTANCE("Принимается");

    private final String value;

    public static OrderStatusDto toOrderDtoStatus(Status status){
        return switch (status){
            case COMPLETED -> OrderStatusDto.COMPLETED;
            case DELIVERY -> OrderStatusDto.DELIVERY;
            case PACKAGING -> OrderStatusDto.PACKAGING;
            case ASSEMBLY -> OrderStatusDto.ASSEMBLY;
            case PREPARATION -> OrderStatusDto.PREPARATION;
            case ACCEPTED -> OrderStatusDto.ACCEPTED;
            case CANCELED -> OrderStatusDto.CANCELED;
            case ACCEPTANCE -> OrderStatusDto.ACCEPTANCE;
        };
    }
}
