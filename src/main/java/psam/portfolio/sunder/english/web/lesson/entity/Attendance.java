package psam.portfolio.sunder.english.web.lesson.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import psam.portfolio.sunder.english.web.student.entity.Student;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Table(name = "attendances")
@Entity
public class Attendance {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    private LocalDateTime attendanceDateTime;
    private boolean attendance;
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_uuid")
    private Lesson lesson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_uuid")
    private Student student;

    @Builder
    public Attendance(LocalDateTime attendanceDateTime, boolean attendance, String comment, Lesson lesson, Student student) {
        this.attendanceDateTime = attendanceDateTime;
        this.attendance = attendance;
        this.comment = comment;
        this.lesson = lesson;
        this.student = student;
    }
}
