package psam.portfolio.sunder.english.domain.teacher.model.request;

import lombok.Builder;
import lombok.Getter;
import psam.portfolio.sunder.english.global.pagination.SearchCond;

@Getter
public class TeacherSearchCond extends SearchCond {

    private final String status;
    private final String studentName;

    @Builder
    public TeacherSearchCond(Integer page, Integer size, String prop, String order, String status, String studentName) {
        super(page, size, prop, order);
        this.status = status;
        this.studentName = studentName;
    }
}
