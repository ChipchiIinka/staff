package ru.egartech.staff.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.egartech.staff.entity.enums.Status;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
public class OrderDetailsEntity {

    private boolean isPaid;

    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToMany
    @ToString.Exclude
    @JoinTable(name = "staff_projects",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "staff_id"))
    private List<StaffEntity> staff;

    @ManyToMany
    @ToString.Exclude
    @JoinTable(name = "order_products",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id"))
    private List<ProductEntity> products;
}
