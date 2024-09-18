package ru.egartech.staff.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "manual")
public class ManualEntity {
    @EmbeddedId
    private ManualId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    @ToString.Exclude
    private ProductEntity product;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("materialId")
    @JoinColumn(name = "material_id")
    @ToString.Exclude
    private MaterialEntity material;

    @NotNull
    private Integer quantity;

    public ManualEntity(ProductEntity product, MaterialEntity material, Integer quantity) {
        this.id = new ManualId(product.getId(), material.getId());
        this.product = product;
        this.material = material;
        this.quantity = quantity;
    }

    public Long getMaterialId(){
        return id.getMaterialId();
    }
}
