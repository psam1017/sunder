package psam.portfolio.sunder.english.domain.teacher.model.request;

import lombok.Builder;
import lombok.Getter;
import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;
import psam.portfolio.sunder.english.global.pagination.PageSearchCond;

@Getter
public class TeacherPageSearchCond extends PageSearchCond {

    private final UserStatus status;

    private final String teacherName;

    @Builder
    public TeacherPageSearchCond(Integer page, Integer size, String prop, String dir, String status, String teacherName) {
        super(page, size, prop, dir);
        this.status = UserStatus.ofNullable(status);
        this.teacherName = teacherName;
    }
}
