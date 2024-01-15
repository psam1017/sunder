package portfolio.sunder.web.student.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import portfolio.sunder.web.student.entity.Student;
import portfolio.sunder.web.user.enumeration.UserStatus;

import java.util.Set;

import static portfolio.sunder.web.user.enumeration.UserRole.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class StudentSave {

    private String uid;
    private String upw;
    private String name;
    private UserStatus status;
    private int grade;
    private int classroom;
    private String school;

    public Student toEntity() {
        return Student.builder()
                .uid(uid)
                .upw(upw)
                .name(name)
                .roles(Set.of(ROLE_STUDENT))
                .status(status)
                .grade(grade)
                .classroom(classroom)
                .school(school)
                .build();
    }
}
