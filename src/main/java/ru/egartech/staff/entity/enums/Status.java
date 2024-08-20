package ru.egartech.staff.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.egartech.staff.entity.OrderEntity;

@Getter
@RequiredArgsConstructor
public enum Status {
    COMPLETED(7),
    DELIVERY(6),
    PACKAGING(5),
    ASSEMBLY(4),
    PREPARATION(3),
    ACCEPTED(2),
    CANCELED(7),
    ACCEPTANCE(1);

    private final int value;

    public static void toNextStatus(OrderEntity order){
        Status status = order.getStatus();
        switch (status.value){
            case 1 -> order.setStatus(Status.ACCEPTED);
            case 2 -> order.setStatus(Status.PREPARATION);
            case 3 -> order.setStatus(Status.ASSEMBLY);
            case 4 -> order.setStatus(Status.PACKAGING);
            case 5 -> order.setStatus(Status.DELIVERY);
            case 6 -> order.setStatus(Status.COMPLETED);
            default -> order.setStatus(Status.CANCELED);
        }
    }
}
