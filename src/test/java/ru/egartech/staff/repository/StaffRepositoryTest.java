package ru.egartech.staff.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.egartech.staff.entity.StaffEntity;
import ru.egartech.staff.entity.enums.Position;
import ru.egartech.staff.entity.enums.Role;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class StaffRepositoryTest {

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private EntityManager em;

    private StaffEntity staff;

    @BeforeEach
    void setUp() {
        staff = new StaffEntity();
        staff.setLogin("testLogin");
        staff.setPassword("testPassword");
        staff.setEmail("test@example.com");
        staff.setPhoneNumber("1234567890");
        staff.setDeleted(false);  // Not banned by default
        staff.setRole(Role.USER);
        staff.setProjects(new ArrayList<>());
        staff.setPosition(Position.MANAGER);
        staff.setFullName("Test User");
        staff = staffRepository.save(staff);
    }

    @Test
    void testMarkAsBanned() {
        staffRepository.markAsBanned(staff.getId());

        em.flush();
        em.clear();

        StaffEntity updatedStaff = staffRepository.findById(staff.getId()).orElseThrow();

        assertTrue(updatedStaff.isDeleted());
    }


    @Test
    void testMarkAsUnbanned() {
        staffRepository.markAsBanned(staff.getId());
        staffRepository.markAsUnbanned(staff.getId());

        em.flush();
        em.clear();

        StaffEntity updatedStaff = staffRepository.findById(staff.getId()).orElseThrow();

        assertFalse(updatedStaff.isDeleted());
    }
}
