package psam.portfolio.sunder.english.domain.study.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

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
    private Boolean correct;
    private String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "study_id",
            nullable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    private Study study;

    // 선택형 단어 학습에서만 사용.
    // 작업 비용을 고려하여 분리하지 않았지만, 필요하다면 단일 테이블 전략으로 분리하고 상속
    @ElementCollection
    @CollectionTable(
            name = "study_word_choices",
            joinColumns = @JoinColumn(name = "study_word_id")
    )
    private List<String> choices = new ArrayList<>();

    @Builder
    public StudyWord(String question, String submit, String answer, Boolean correct, String reason, Study study) {
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

    public void submit(String submit) {
        if (StringUtils.hasText(submit)) {
            this.submit = submit.trim();
            this.correct = this.getStudy().isIgnoreCase()
                    ? this.getAnswer().equalsIgnoreCase(this.submit)
                    : this.getAnswer().equals(this.submit);
        } else {
            this.correct = false;
        }
    }

    public void correct(Boolean correct, String reason) {
        this.correct = correct;
        this.reason = reason;
    }
}
