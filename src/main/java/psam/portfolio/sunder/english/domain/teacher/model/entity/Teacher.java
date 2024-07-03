package psam.portfolio.sunder.english.domain.teacher.model.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import psam.portfolio.sunder.english.domain.student.model.entity.Student;
import psam.portfolio.sunder.english.domain.study.model.entity.Study;
import psam.portfolio.sunder.english.global.jpa.embeddable.Address;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.user.model.enumeration.RoleName;
import psam.portfolio.sunder.english.domain.user.model.enumeration.UserStatus;
import psam.portfolio.sunder.english.domain.user.model.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@PrimaryKeyJoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
@DiscriminatorValue("TEACHER")
@Table(
        name = "teachers",
        indexes = @Index(columnList = "academy_id")
)
@Entity
public class Teacher extends User {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "academy_id",
            nullable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    private Academy academy;

    @OneToMany(mappedBy = "teacher")
    private List<Study> studies = new ArrayList<>();

    @Builder
    public Teacher(String loginId, String loginPw, String name, String email, boolean emailVerified, String phone, Address address, UserStatus status, Academy academy) {
        super(loginId, loginPw, name, email, emailVerified, phone, address, status);
        this.academy = academy;
    }

    public boolean isDirector() {
        return this.getRoles().stream().anyMatch(role -> role.getRoleName() == RoleName.ROLE_DIRECTOR);
    }

    public boolean hasSameAcademy(Teacher teacher) {
        return Objects.equals(this.getAcademy().getId(), teacher.getAcademy().getId());
    }

    public boolean hasSameAcademy(Student student) {
        return Objects.equals(this.getAcademy().getId(), student.getAcademy().getId());
    }
}
