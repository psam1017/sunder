package psam.portfolio.sunder.english.web.teacher.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import psam.portfolio.sunder.english.global.jpa.audit.BaseEntity;
import psam.portfolio.sunder.english.global.jpa.embeddable.Address;
import psam.portfolio.sunder.english.web.student.model.Student;
import psam.portfolio.sunder.english.web.teacher.enumeration.AcademyStatus;

import java.util.Set;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Table(name = "academies")
@Entity
public class Academy extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @Column(unique = true, nullable = false)
    private String name;

    @Embedded
    private Address address;

    @Column(unique = true)
    private String phone;

    @Column(unique = true)
    private String email;

    private boolean openToPublic;

    @Enumerated(EnumType.STRING)
    private AcademyStatus status;

    @OneToMany(mappedBy = "academy")
    private Set<Teacher> teachers;

    @OneToMany(mappedBy = "academy")
    private Set<Student> students;

    @Builder
    public Academy(String name, Address address, String phone, String email, boolean openToPublic, AcademyStatus status) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.openToPublic = openToPublic;
        this.status = status;
    }
}
