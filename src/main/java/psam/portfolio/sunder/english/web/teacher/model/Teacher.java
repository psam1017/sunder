package psam.portfolio.sunder.english.web.teacher.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import psam.portfolio.sunder.english.global.jpa.embeddable.Address;
import psam.portfolio.sunder.english.web.lesson.model.Lesson;
import psam.portfolio.sunder.english.web.user.model.User;
import psam.portfolio.sunder.english.web.user.model.UserRole;
import psam.portfolio.sunder.english.web.user.enumeration.UserStatus;

import java.util.Set;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@DiscriminatorValue("TEACHER")
@Table(name = "teachers")
@Entity
public class Teacher extends User {

    @ManyToOne(fetch = LAZY)
    private Academy academy;

    @OneToMany(mappedBy = "teacher")
    private Set<Lesson> lessons;

    @Builder
    public Teacher(String loginId, String loginPw, String name, String email, boolean emailVerified, String phone, Address address, UserStatus status, Set<UserRole> roles, Academy academy) {
        super(loginId, loginPw, name, email, emailVerified, phone, address, status, roles);
        this.academy = academy;
    }

    public void addLesson(Lesson lesson) {
        this.lessons.add(lesson);
    }

    public void removeLesson(Lesson lesson) {
        this.lessons.remove(lesson);
    }
}
