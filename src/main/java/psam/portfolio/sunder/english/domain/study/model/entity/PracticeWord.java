package psam.portfolio.sunder.english.domain.study.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("PRACTICE_WORD")
@Table(
        name = "practice_words",
        indexes = @Index(columnList = "practice_id")
)
@Entity
public class PracticeWord extends StudyWord {

    private int retryCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "practice_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Practice practice;

    @Builder
    public PracticeWord(String studentAnswer, String korean, String english, boolean correct, String incorrectReason, int retryCount, Practice practice) {
        super(studentAnswer, korean, english, correct, incorrectReason);
        this.retryCount = retryCount;
        this.practice = practice;
    }
}
