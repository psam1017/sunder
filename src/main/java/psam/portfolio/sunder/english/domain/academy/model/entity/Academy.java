package psam.portfolio.sunder.english.domain.academy.model.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;
import psam.portfolio.sunder.english.domain.academy.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.domain.book.model.entity.Book;
import psam.portfolio.sunder.english.domain.student.model.entity.Student;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.domain.user.model.entity.User;
import psam.portfolio.sunder.english.global.jpa.audit.TimeEntity;
import psam.portfolio.sunder.english.global.jpa.embeddable.Address;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Table(name = "academies")
@Entity
public class Academy extends TimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

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

    @OneToMany(mappedBy = "academy")
    private List<Book> books = new ArrayList<>();

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

    public boolean isForbidden() {
        return this.status == AcademyStatus.FORBIDDEN;
    }

    public boolean isWithdrawn() {
        return this.status == AcademyStatus.WITHDRAWN;
    }

    public boolean hasUser(User proxy) {
        User user = Hibernate.unproxy(proxy, User.class);
        if (user instanceof Teacher t) {
            return Objects.equals(t.getAcademy().getId(), this.id);
        } else if (user instanceof Student s) {
            return Objects.equals(s.getAcademy().getId(), this.id);
        }
        return false;
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
