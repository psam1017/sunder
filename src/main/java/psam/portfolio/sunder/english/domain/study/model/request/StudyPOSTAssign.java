package psam.portfolio.sunder.english.domain.study.model.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import psam.portfolio.sunder.english.domain.student.model.entity.Student;
import psam.portfolio.sunder.english.domain.study.model.entity.Study;
import psam.portfolio.sunder.english.domain.study.enumeration.StudyClassification;
import psam.portfolio.sunder.english.domain.study.enumeration.StudyStatus;
import psam.portfolio.sunder.english.domain.study.enumeration.StudyTarget;
import psam.portfolio.sunder.english.domain.study.enumeration.StudyType;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class StudyPOSTAssign extends StudyPOSTStart {

    @NotEmpty
    private List<UUID> studentIds;

    @NotNull
    private Boolean shuffleEach;

    @Builder(builderMethodName = "assignBuilder")
    public StudyPOSTAssign(List<UUID> bookIds, Boolean ignoreCase, Integer numberOfWords, StudyType type, StudyClassification classification, StudyTarget target, List<UUID> studentIds, Boolean shuffleEach) {
        super(bookIds, ignoreCase, numberOfWords, type, classification, target);
        this.studentIds = studentIds;
        this.shuffleEach = shuffleEach;
    }

    @Override
    public Study toEntity(long sequence, Student student, String title) {
        return Study.builder()
                .sequence(sequence)
                .ignoreCase(ignoreCase == null || ignoreCase)
                .title(title)
                .status(StudyStatus.ASSIGNED) // status = ASSIGNED
                .type(super.getType())
                .classification(super.getClassification())
                .target(super.getTarget())
                .student(student)
                .build();
    }
}
