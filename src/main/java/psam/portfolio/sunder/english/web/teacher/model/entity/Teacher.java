package psam.portfolio.sunder.english.web.teacher.model.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import psam.portfolio.sunder.english.global.jpa.embeddable.Address;
import psam.portfolio.sunder.english.web.academy.model.entity.Academy;
import psam.portfolio.sunder.english.web.user.enumeration.RoleName;
import psam.portfolio.sunder.english.web.user.enumeration.UserStatus;
import psam.portfolio.sunder.english.web.user.model.entity.User;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@DiscriminatorValue("TEACHER")
@Table(name = "teachers")
@Entity
public class Teacher extends User {

    @ManyToOne(fetch = LAZY)
    private Academy academy;

    @Builder
    public Teacher(String loginId, String loginPw, String name, String email, boolean emailVerified, String phone, Address address, UserStatus status, Academy academy) {
        super(loginId, loginPw, name, email, emailVerified, phone, address, status);
        this.academy = academy;
    }

    public boolean isDirector() {
        return this.getRoles().stream().anyMatch(role -> role.getRoleName() == RoleName.ROLE_DIRECTOR);
    }
}
