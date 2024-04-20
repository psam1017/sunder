package psam.portfolio.sunder.english.domain.study.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.student.model.entity.Student;
import psam.portfolio.sunder.english.global.jpa.audit.TimeEntity;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(
        name = "studies",
        indexes = {
                @Index(columnList = "academy_id"),
                @Index(columnList = "student_id")
        }
)
@Entity
public abstract class Study extends TimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String publisher;
    private String bookName;
    private String chapter;
    private String subject;
    private int score;

    private LocalDateTime submitDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Student student;

    // 반정규화 필드
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academy_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Academy academy;

    protected Study(String publisher, String bookName, String chapter, String subject, Student student, Academy academy) {
        this.publisher = publisher;
        this.bookName = bookName;
        this.chapter = chapter;
        this.subject = subject;
        this.student = student;
        this.academy = academy;
        this.score = 0;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void updateSubmitDateTime() {
        this.submitDateTime = LocalDateTime.now();
    }
}
