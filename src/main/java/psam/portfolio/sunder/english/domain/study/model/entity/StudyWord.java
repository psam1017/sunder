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
        indexes = @Index(columnList = "study_id")
)
@Entity
public class StudyWord {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String question;
    private String submit;
    private String answer;
    private boolean correct;
    private String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "study_id",
            nullable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    private Study study;

    @Builder
    public StudyWord(String question, String submit, String answer, boolean correct, String reason, Study study) {
        this.question = question;
        this.submit = submit;
        this.answer = answer;
        this.correct = correct;
        this.reason = reason;
        this.study = study;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public void setReason(String incorrectReason) {
        this.reason = incorrectReason;
    }
}
