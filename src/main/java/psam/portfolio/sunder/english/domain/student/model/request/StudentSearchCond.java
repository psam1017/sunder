package psam.portfolio.sunder.english.domain.student.model.request;

import lombok.Builder;
import lombok.Getter;
import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;
import psam.portfolio.sunder.english.global.pagination.SearchCond;

@Getter
public class StudentSearchCond extends SearchCond {

    private final String address;

    private final UserStatus status;

    private final String studentName;

    private final String attendanceId;

    private final String schoolName;

    private final String grade;

    private final String parentName;

    @Builder
    public StudentSearchCond(Integer page, Integer size, String prop, String dir, String address, String status, String studentName, String attendanceId, String schoolName, String grade, String parentName) {
        super(page, size, prop, dir);
        this.address = address;
        this.status = UserStatus.ofNullable(status);
        this.studentName = studentName;
        this.attendanceId = attendanceId;
        this.schoolName = schoolName;
        this.grade = grade;
        this.parentName = parentName;
    }
}
