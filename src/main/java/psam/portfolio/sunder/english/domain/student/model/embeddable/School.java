package psam.portfolio.sunder.english.domain.student.model.embeddable;

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
    private Integer schoolGrade;

    @Builder
    public School(String schoolName, Integer schoolGrade) {
        this.schoolName = schoolName;
        this.schoolGrade = schoolGrade;
    }
}
