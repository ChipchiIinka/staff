package ru.egartech.staff.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Embeddable
public class ManualId implements Serializable {
    private Long productId;
    private Long materialId;
}
