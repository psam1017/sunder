package psam.portfolio.sunder.english.domain.student.model.request;

import lombok.Builder;
import lombok.Getter;
import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;
import psam.portfolio.sunder.english.global.pagination.PageSearchCond;

@Getter
public class StudentPageSearchCond extends PageSearchCond {

    private final String address;

    private final UserStatus status;

    private final String name;

    private final String attendanceId;

    private final String schoolName;

    private final Integer schoolGrade;

    private final String parentName;

    @Builder
    public StudentPageSearchCond(Integer page, Integer size, String prop, String dir, String address, String status, String name, String attendanceId, String schoolName, Integer schoolGrade, String parentName) {
        super(page, size, prop, dir);
        this.address = address;
        this.status = UserStatus.ofNullable(status);
        this.name = name;
        this.attendanceId = attendanceId;
        this.schoolName = schoolName;
        this.schoolGrade = schoolGrade;
        this.parentName = parentName;
    }
}
