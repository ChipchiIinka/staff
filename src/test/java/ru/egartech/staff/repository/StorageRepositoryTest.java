package ru.egartech.staff.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.egartech.staff.entity.StorageEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@DataJpaTest
@ActiveProfiles("test")
class StorageRepositoryTest {

    @Autowired
    private StorageRepository storageRepository;

    @Test
    void testSaveAndFindById() {
        StorageEntity storageEntity = new StorageEntity();
        storageEntity.setAddress("Test Storage Address");

        StorageEntity savedEntity = storageRepository.save(storageEntity);
        StorageEntity foundEntity = storageRepository.findById(savedEntity.getId()).orElse(null);

        assertThat(foundEntity).isNotNull();
        assertThat(foundEntity.getAddress()).isEqualTo("Test Storage Address");
    }

    @Test
    void testDelete() {
        StorageEntity storageEntity = new StorageEntity();
        storageEntity.setAddress("Test Storage Address");
        StorageEntity savedEntity = storageRepository.save(storageEntity);
        assertNotNull(storageRepository.findById(savedEntity.getId()));
        storageRepository.deleteById(savedEntity.getId());

        assertNull(storageRepository.findById(savedEntity.getId()).orElse(null));
    }
}