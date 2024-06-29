package psam.portfolio.sunder.english.domain.study.model.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import psam.portfolio.sunder.english.domain.book.model.entity.Book;
import psam.portfolio.sunder.english.domain.student.model.entity.Student;
import psam.portfolio.sunder.english.domain.study.model.enumeration.StudyClassification;
import psam.portfolio.sunder.english.domain.study.model.enumeration.StudyStatus;
import psam.portfolio.sunder.english.domain.study.model.enumeration.StudyType;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.global.jpa.audit.TimeEntity;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Table(
        name = "studies",
        indexes = {
                @Index(columnList = "academy_id"),
                @Index(columnList = "student_id")
        }
)
@Entity
public class Study extends TimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(updatable = false)
    private String bookPublisher;
    @Column(updatable = false)
    private String bookName;
    @Column(updatable = false)
    private String bookChapter;
    @Column(updatable = false)
    private String bookSubject;
    @Enumerated(EnumType.STRING)
    private StudyStatus status;
    @Enumerated(EnumType.STRING)
    private StudyType type;
    @Enumerated(EnumType.STRING)
    private StudyClassification classification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Teacher teacher;

    @Builder
    public Study(StudyStatus status, StudyType type, StudyClassification classification, Book book, Student student, Teacher teacher) {
        this.bookPublisher = book.getPublisher();
        this.bookName = book.getName();
        this.bookChapter = book.getChapter();
        this.bookSubject = book.getSubject();
        this.status = status;
        this.type = type;
        this.classification = classification;
        this.book = book;
        this.student = student;
        this.teacher = teacher;
    }
}
