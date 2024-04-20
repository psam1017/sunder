package psam.portfolio.sunder.english.domain.study.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(
        name = "study_words"
)
@Entity
public class StudyWord {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String studentAnswer;
    private String korean;
    private String english;
    private boolean correct;
    private String incorrectReason;

    protected StudyWord(String studentAnswer, String korean, String english, boolean correct, String incorrectReason) {
        this.studentAnswer = studentAnswer;
        this.korean = korean;
        this.english = english;
        this.correct = correct;
        this.incorrectReason = incorrectReason;
    }
}
