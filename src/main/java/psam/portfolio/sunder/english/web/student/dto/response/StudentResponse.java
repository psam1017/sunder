package psam.portfolio.sunder.english.web.student.dto.response;

import lombok.Getter;
import psam.portfolio.sunder.english.web.student.entity.Student;
import psam.portfolio.sunder.english.web.user.enumeration.UserStatus;

@Getter
public class StudentResponse {

    // upw 는 반환하지 않는다.
    // roles 는 반환하지 않는다.
    private Long id;
    private String uid;
    private String name;
    private UserStatus status;
    private int grade;
    private int classroom;
    private String school;

    public StudentResponse(Student student) {
        if (student != null) {
            this.id = student.getId();
            this.uid = student.getUid();
            this.name = student.getName();
            this.status = student.getStatus();
            this.grade = student.getGrade();
            this.classroom = student.getClassroom();
            this.school = student.getSchool();
        }
    }
}
