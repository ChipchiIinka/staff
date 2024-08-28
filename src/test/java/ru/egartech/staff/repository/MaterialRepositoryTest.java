package ru.egartech.staff.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.egartech.staff.entity.MaterialEntity;
import ru.egartech.staff.entity.enums.MaterialType;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class MaterialRepositoryTest {

    @Autowired
    private MaterialRepository materialRepository;

    @Test
    void testSaveMaterial() {
        MaterialEntity material = new MaterialEntity();
        material.setName("Wood");
        material.setDescription("High quality wood");
        material.setLength(100);
        material.setWidth(50);
        material.setHeight(25);
        material.setType(MaterialType.WOOD);

        MaterialEntity savedMaterial = materialRepository.save(material);

        assertThat(savedMaterial.getId()).isNotNull();
    }

    @Test
    void testFindById() {
        MaterialEntity material = new MaterialEntity();
        material.setName("Bamboo");
        material.setDescription("Eco-friendly bamboo");
        material.setLength(200);
        material.setWidth(30);
        material.setHeight(20);
        material.setType(MaterialType.BAMBOO);

        MaterialEntity savedMaterial = materialRepository.save(material);

        Optional<MaterialEntity> foundMaterial = materialRepository.findById(savedMaterial.getId());

        assertThat(foundMaterial).isPresent();
        assertThat(foundMaterial.get().getName()).isEqualTo("Bamboo");
    }

    @Test
    void testDeleteMaterial() {
        MaterialEntity material = new MaterialEntity();
        material.setName("Glass");
        material.setDescription("Clear glass");
        material.setLength(120);
        material.setWidth(60);
        material.setHeight(10);
        material.setType(MaterialType.GLASS);

        MaterialEntity savedMaterial = materialRepository.save(material);

        materialRepository.deleteById(savedMaterial.getId());

        Optional<MaterialEntity> deletedMaterial = materialRepository.findById(savedMaterial.getId());
        assertThat(deletedMaterial).isNotPresent();
    }
}