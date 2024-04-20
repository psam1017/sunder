package psam.portfolio.sunder.english.domain.exam.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import psam.portfolio.sunder.english.domain.study.model.entity.StudyWord;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("EXAM_WORD")
@PrimaryKeyJoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
@Table(
        name = "exam_words",
        indexes = @Index(columnList = "exam_id")
)
@Entity
public class ExamWord extends StudyWord {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Exam exam;

    @Builder
    public ExamWord(String studentAnswer, String korean, String english, boolean correct, String incorrectReason, Exam exam) {
        super(studentAnswer, korean, english, correct, incorrectReason);
        this.exam = exam;
    }
}
