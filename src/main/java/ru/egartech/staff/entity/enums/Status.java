package ru.egartech.staff.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.egartech.staff.model.OrderStatusDto;

import java.util.List;

@Getter
@RequiredArgsConstructor
public enum Status {
    COMPLETED("Завершен"),
    DELIVERY("Доставка"),
    WAITING_FOR_DELIVERY("Ожидание доставки"),
    PACKAGING("Упаковка"),
    ASSEMBLY("Сборка"),
    PREPARATION("Подготовка"),
    ACCEPTED("Принят"),
    CANCELED("Отменен");

    private final String value;

    public static OrderStatusDto toOrderDtoStatus(Status status){
        return switch (status){
            case COMPLETED -> OrderStatusDto.COMPLETED;
            case DELIVERY -> OrderStatusDto.DELIVERY;
            case WAITING_FOR_DELIVERY -> OrderStatusDto.WAITING_FOR_DELIVERY;
            case PACKAGING -> OrderStatusDto.PACKAGING;
            case ASSEMBLY -> OrderStatusDto.ASSEMBLY;
            case PREPARATION -> OrderStatusDto.PREPARATION;
            case ACCEPTED -> OrderStatusDto.ACCEPTED;
            case CANCELED -> OrderStatusDto.CANCELED;
        };
    }

    public static final List<Status> canRestart = List.of(COMPLETED, DELIVERY, WAITING_FOR_DELIVERY, PACKAGING, ASSEMBLY);
}
