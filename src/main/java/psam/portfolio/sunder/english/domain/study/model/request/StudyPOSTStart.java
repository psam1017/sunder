package psam.portfolio.sunder.english.domain.study.model.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;
import psam.portfolio.sunder.english.domain.student.model.entity.Student;
import psam.portfolio.sunder.english.domain.study.model.entity.Study;
import psam.portfolio.sunder.english.domain.study.model.enumeration.StudyClassification;
import psam.portfolio.sunder.english.domain.study.model.enumeration.StudyStatus;
import psam.portfolio.sunder.english.domain.study.model.enumeration.StudyTarget;
import psam.portfolio.sunder.english.domain.study.model.enumeration.StudyType;
import psam.portfolio.sunder.english.global.validator.EnumPattern;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyPOSTStart {

    @NotEmpty
    @Size(min = 1, max = 20)
    private List<UUID> bookIds;

    Boolean ignoreCase;

    @NotNull
    @Range(min = 1, max = 100)
    private Integer numberOfWords;

    @NotNull
    @EnumPattern(regexp = "^(TRACING|SELECT|WRITING)$")
    private StudyType type;

    @NotNull
    @EnumPattern(regexp = "^(EXAM|PRACTICE)$")
    private StudyClassification classification;

    @NotNull
    @EnumPattern(regexp = "^(KOREAN|ENGLISH)$")
    private StudyTarget target;

    public Study toEntity(long sequence, Student student, String title) {
        return Study.builder()
                .sequence(sequence)
                .ignoreCase(ignoreCase)
                .title(title)
                .status(StudyStatus.STARTED)
                .type(type)
                .classification(classification)
                .target(target)
                .student(student)
                .build();
    }
}
