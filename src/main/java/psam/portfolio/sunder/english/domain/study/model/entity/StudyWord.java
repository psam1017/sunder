package psam.portfolio.sunder.english.domain.study.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(
        name = "study_words",
        indexes = {
                @Index(columnList = "practice_id"),
                @Index(columnList = "exam_id")
        }
)
@Entity
public class StudyWord {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String submit;
    private String question;
    private String answer;
    private boolean correct;
    private String incorrectReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Study study;

    @Builder
    public StudyWord(String submit, String question, String answer, boolean correct, String incorrectReason, Study study) {
        this.submit = submit;
        this.question = question;
        this.answer = answer;
        this.correct = correct;
        this.incorrectReason = incorrectReason;
        this.study = study;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public void setIncorrectReason(String incorrectReason) {
        this.incorrectReason = incorrectReason;
    }
}
