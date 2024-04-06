package psam.portfolio.sunder.english.domain.student.model.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Embeddable
public class School {

    @Column(name = "school_name")
    private String name;
    @Column(name = "school_grade")
    private Integer grade;

    @Builder
    public School(String name, Integer grade) {
        this.name = name;
        this.grade = grade;
    }
}
