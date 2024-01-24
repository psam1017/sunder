package psam.portfolio.sunder.english.web.lesson.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import psam.portfolio.sunder.english.web.lesson.enumeration.DayOfWeek;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Table(name = "lesson_times")
@Entity
public class LessonTime {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    private String startTime;
    private String endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_uuid")
    private Lesson lesson;

    @Builder
    public LessonTime(DayOfWeek dayOfWeek, String startTime, String endTime, Lesson lesson) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.lesson = lesson;
    }
}
