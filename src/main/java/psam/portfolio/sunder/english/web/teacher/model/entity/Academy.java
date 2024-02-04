package psam.portfolio.sunder.english.web.teacher.model.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import psam.portfolio.sunder.english.global.jpa.audit.BaseEntity;
import psam.portfolio.sunder.english.global.jpa.embeddable.Address;
import psam.portfolio.sunder.english.web.student.model.entity.Student;
import psam.portfolio.sunder.english.web.teacher.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.web.user.model.entity.User;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Table(name = "academies")
@Entity
public class Academy extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @Column(nullable = false)
    private String name;

    @Embedded
    private Address address;

    @Column
    private String phone;

    @Column
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

    public void verify() {
        this.status = AcademyStatus.VERIFIED;
    }

    public boolean isPending() {
        return this.status == AcademyStatus.PENDING;
    }

    public boolean isVerified() {
        return this.status == AcademyStatus.VERIFIED;
    }

    public boolean isSuspended() {
        return this.status == AcademyStatus.SUSPENDED;
    }

    public boolean hasTeacher(User user) {
        return this.teachers.stream().anyMatch(teacher -> Objects.equals(teacher.getUuid(), user.getUuid()));
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setOpenToPublic(Boolean openToPublic) {
        this.openToPublic = openToPublic;
    }
}
