package ru.egartech.staff.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.egartech.staff.entity.enums.Position;
import ru.egartech.staff.entity.enums.Role;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "staff")
public class StaffEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String login;

    private String email;

    private String password;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    private Role role;

    private Position position;

    @Column(name = "fullname")
    private String fullName;
}
