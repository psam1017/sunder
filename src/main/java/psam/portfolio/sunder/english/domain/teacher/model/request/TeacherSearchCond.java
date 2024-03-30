package psam.portfolio.sunder.english.domain.teacher.model.request;

import lombok.Builder;
import lombok.Getter;
import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;
import psam.portfolio.sunder.english.global.validator.EnumPattern;
import psam.portfolio.sunder.english.global.pagination.SearchCond;

@Getter
public class TeacherSearchCond extends SearchCond {

    @EnumPattern(regexp = "^(PENDING|TRIAL|TRIAL_END|ACTIVE|FORBIDDEN|WITHDRAWN)$", nullable = true)
    private final UserStatus status;

    private final String teacherName;

    @Builder
    public TeacherSearchCond(Integer page, Integer size, String prop, String dir, UserStatus status, String teacherName) {
        super(page, size, prop, dir);
        this.status = status;
        this.teacherName = teacherName;
    }
}
