package psam.portfolio.sunder.english.domain.study.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
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
public abstract class StudyWord {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String submit;
    private String question;
    private String answer;
    private boolean correct;
    private String incorrectReason;

    protected StudyWord(String submit, String question, String answer, boolean correct, String incorrectReason) {
        this.submit = submit;
        this.question = question;
        this.answer = answer;
        this.correct = correct;
        this.incorrectReason = incorrectReason;
    }
}
