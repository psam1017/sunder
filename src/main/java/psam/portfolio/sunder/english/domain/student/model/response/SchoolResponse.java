package psam.portfolio.sunder.english.domain.student.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import psam.portfolio.sunder.english.domain.student.model.embeddable.School;

import static lombok.AccessLevel.PRIVATE;

@Builder
@AllArgsConstructor(access = PRIVATE)
@Getter
public class SchoolResponse {

    private String name;
    private Integer grade;

    public static SchoolResponse from(School school) {
        if (school == null) {
            return new SchoolResponse(null, null);
        }
        return SchoolResponse.builder()
                .name(school.getSchoolName())
                .grade(school.getGrade())
                .build();
    }
}
