package portfolio.sunder.domain.student.dto.response;

import lombok.Getter;
import portfolio.sunder.domain.student.entity.Student;

@Getter
public class StudentResponse {

    private Long id;
    private String uid;
    // upw 는 반환하지 않는다.
    private String name;
    private String status;
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
