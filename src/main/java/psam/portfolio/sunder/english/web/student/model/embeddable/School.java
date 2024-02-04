package psam.portfolio.sunder.english.web.student.model.embeddable;

import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Embeddable
public class School {

    private String schoolName;
    private Integer grade;

    @Builder
    public School(String schoolName, Integer grade) {
        this.schoolName = schoolName;
        this.grade = grade;
    }
}
