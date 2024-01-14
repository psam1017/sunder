package portfolio.sunder.domain.student.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import portfolio.sunder.domain.user.entity.User;
import portfolio.sunder.domain.user.enumeration.UserRole;
import portfolio.sunder.domain.user.enumeration.UserStatus;

import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("STUDENT")
@Entity
public class Student extends User {

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
