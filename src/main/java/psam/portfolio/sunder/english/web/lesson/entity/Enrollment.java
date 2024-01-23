package psam.portfolio.sunder.english.web.lesson.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import psam.portfolio.sunder.english.global.audit.BaseEntity;
import psam.portfolio.sunder.english.web.lesson.enumeration.EnrollmentStatus;
import psam.portfolio.sunder.english.web.lesson.enumeration.Grade;
import psam.portfolio.sunder.english.web.student.entity.Student;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Entity
public class Enrollment extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @Enumerated(EnumType.STRING)
    private EnrollmentStatus status;

    private LocalDateTime enrolledDateTime;

    @Enumerated(EnumType.STRING)
    private Grade grade;

    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_uuid")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_uuid")
    private Lesson lesson;

    @Builder
    public Enrollment(EnrollmentStatus status, LocalDateTime enrolledDateTime, Grade grade, String comment, Student student, Lesson lesson) {
        this.status = status;
        this.enrolledDateTime = enrolledDateTime;
        this.grade = grade;
        this.comment = comment;
        this.student = student;
        this.lesson = lesson;
    }
}