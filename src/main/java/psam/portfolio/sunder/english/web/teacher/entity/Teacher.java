package psam.portfolio.sunder.english.web.teacher.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import psam.portfolio.sunder.english.global.entity.embeddable.Address;
import psam.portfolio.sunder.english.web.lesson.entity.Lesson;
import psam.portfolio.sunder.english.web.user.entity.User;
import psam.portfolio.sunder.english.web.user.entity.UserRole;
import psam.portfolio.sunder.english.web.user.enumeration.UserStatus;

import java.util.Set;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Table(name = "teachers")
@Entity
@DiscriminatorValue("TEACHER")
public class Teacher extends User {

    private String email;
    private boolean emailVerified;

    @OneToMany(mappedBy = "teacher")
    private Set<Lesson> lessons;

    @ManyToOne(fetch = LAZY)
    private Academy academy;

    @Builder
    public Teacher(String loginId, String loginPw, String name, String phone, Address address, UserStatus status, Set<UserRole> roles, String email, boolean emailVerified, Academy academy) {
        super(loginId, loginPw, name, phone, address, status, roles);
        this.email = email;
        this.emailVerified = emailVerified;
        this.academy = academy;
    }

    public void addLesson(Lesson lesson) {
        this.lessons.add(lesson);
    }

    public void removeLesson(Lesson lesson) {
        this.lessons.remove(lesson);
    }

    public void verifyEmail() {
        this.emailVerified = true;
    }
}
