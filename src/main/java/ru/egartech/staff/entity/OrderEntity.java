package ru.egartech.staff.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.egartech.staff.entity.enums.Status;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String address;

    private LocalDate date;

    @ManyToOne
    @JoinColumn
    private StaffEntity manager;

    private Status status;

    @ManyToMany
    @JoinTable(name = "staff_projects",
         joinColumns = @JoinColumn(name = "staff_id"),
         inverseJoinColumns = @JoinColumn(name = "order_id"))
    @ToString.Exclude
    private List<StaffEntity> staff;

    @ManyToMany
    @JoinTable(name = "order_products",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "order_id"))
    @ToString.Exclude
    private List<ProductEntity> products;
}
