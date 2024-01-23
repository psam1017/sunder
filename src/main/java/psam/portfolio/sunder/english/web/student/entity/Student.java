package psam.portfolio.sunder.english.web.student.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import psam.portfolio.sunder.english.web.user.entity.User;
import psam.portfolio.sunder.english.web.user.enumeration.UserStatus;
import psam.portfolio.sunder.english.web.user.entity.UserRole;

import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("STUDENT")
@Entity
public class Student extends User {

    // TODO: 2024-01-22 Embedded 로 변경

    private int grade;

    private int classroom;

    private String school;

    @Builder
    public Student(String uid, String upw, String name, UserStatus status, Set<UserRole> roles, int grade, int classroom, String school) {
        super(uid, upw, name, status, roles);
        this.grade = grade;
        this.classroom = classroom;
        this.school = school;
    }
}
