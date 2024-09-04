package ru.egartech.staff.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.egartech.staff.entity.enums.MaterialType;

@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "materials")
public class MaterialEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private Integer length;

    private Integer width;

    private Integer height;

    @Enumerated(EnumType.STRING)
    private MaterialType type;
}
