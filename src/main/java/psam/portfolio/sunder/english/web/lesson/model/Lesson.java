package psam.portfolio.sunder.english.web.lesson.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import psam.portfolio.sunder.english.global.entity.audit.BaseEntity;
import psam.portfolio.sunder.english.web.teacher.model.Teacher;
import psam.portfolio.sunder.english.web.lesson.enumeration.LessonStatus;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Table(name = "lessons")
@Entity
public class Lesson extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    private String name;
    private String description;
    private String location;

    private LocalDate openDate;
    private LocalDate closeDate;

    @Enumerated(EnumType.STRING)
    private LessonStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_uuid")
    private Teacher teacher;

    @OneToMany(mappedBy = "lesson")
    private Set<LessonTime> lessonTimes;

    @OneToMany(mappedBy = "lesson")
    private Set<Enrollment> enrollments;

    @OneToMany(mappedBy = "lesson")
    private Set<Attendance> attendances;

    @Builder
    public Lesson(String name, String description, String location, LocalDate openDate, LocalDate closeDate, LessonStatus status, Teacher teacher) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.openDate = openDate;
        this.closeDate = closeDate;
        this.status = status;
        this.teacher = teacher;
    }
}
