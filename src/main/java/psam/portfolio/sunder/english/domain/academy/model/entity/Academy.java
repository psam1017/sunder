package psam.portfolio.sunder.english.domain.academy.model.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import psam.portfolio.sunder.english.global.jpa.audit.TimeEntity;
import psam.portfolio.sunder.english.global.jpa.embeddable.Address;
import psam.portfolio.sunder.english.domain.student.model.entity.Student;
import psam.portfolio.sunder.english.domain.academy.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.domain.user.model.entity.User;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Table(name = "academies")
@Entity
public class Academy extends TimeEntity {

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

    private LocalDateTime withdrawalAt;

    @OneToMany(mappedBy = "academy")
    private List<Teacher> teachers = new ArrayList<>();

    @OneToMany(mappedBy = "academy")
    private List<Student> students = new ArrayList<>();

    @Builder
    public Academy(String name, Address address, String phone, String email, boolean openToPublic, AcademyStatus status) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.openToPublic = openToPublic;
        this.status = status;
        this.withdrawalAt = null;
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

    public boolean isWithdrawn() {
        return this.status == AcademyStatus.WITHDRAWN;
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

    public void setStatus(AcademyStatus status) {
        this.status = status;
    }

    public void setWithdrawalAt(LocalDateTime withdrawalAt) {
        this.withdrawalAt = withdrawalAt;
    }
}
