package psam.portfolio.sunder.english.domain.study.model.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import psam.portfolio.sunder.english.domain.student.model.entity.Student;
import psam.portfolio.sunder.english.domain.study.model.embeddable.StudyRange;
import psam.portfolio.sunder.english.domain.study.enumeration.StudyClassification;
import psam.portfolio.sunder.english.domain.study.enumeration.StudyStatus;
import psam.portfolio.sunder.english.domain.study.enumeration.StudyTarget;
import psam.portfolio.sunder.english.domain.study.enumeration.StudyType;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.global.jpa.audit.BaseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@SQLRestriction(value = "status != 'DELETED'")
@Table(
        name = "studies",
        indexes = {
                @Index(columnList = "student_id"),
                @Index(columnList = "teacher_id"),
                @Index(columnList = "sequence"),
                @Index(columnList = "created_date_time")
        }
)
@Entity
public class Study extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(
            nullable = false,
            unique = true,
            updatable = false
    )
    private long sequence;
    @Column(nullable = false)
    private boolean ignoreCase;
    @Column(nullable = false)
    private String title;
    @Enumerated(EnumType.STRING)
    private StudyStatus status;
    @Enumerated(EnumType.STRING)
    private StudyType type;
    @Enumerated(EnumType.STRING)
    private StudyClassification classification;
    @Enumerated(EnumType.STRING)
    private StudyTarget target;
    private LocalDateTime submitDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "student_id",
            nullable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Teacher teacher;

    // ddl-auto 로는 컬렉션 테이블에 인덱스가 생성되지 않기에 외래키를 부여한다. 만약 인덱스를 생성하고 싶다면 다음 2가지를 고려한다.
    // 1. 엔티티로 만들고 다대일 관계를 생성한다. 이때 orphanRemoval = true, cascade = CascadeType.ALL 을 설정하면 컬렉션 테이블을 만드는 것과 같은 효과를 낼 수 있다.
    // 2. database 에서 직접 인덱스를 생성한다.
    @ElementCollection
    @CollectionTable(
            name = "study_ranges",
            joinColumns = @JoinColumn(name = "study_id")
    )
    private List<StudyRange> studyRanges = new ArrayList<>();

    @OneToMany(mappedBy = "study")
    @OrderBy("id asc")
    private List<StudyWord> studyWords = new ArrayList<>();

    @Builder
    public Study(long sequence, Boolean ignoreCase, String title, StudyStatus status, StudyType type, StudyClassification classification, StudyTarget target, Student student, Teacher teacher) {
        this.sequence = sequence;
        this.ignoreCase = ignoreCase == null || ignoreCase;
        this.title = title;
        this.status = status;
        this.type = type;
        this.classification = classification;
        this.target = target;
        this.student = student;
        this.teacher = teacher;
    }

    public void delete() {
        this.status = StudyStatus.DELETED;
    }

    public boolean canSubmit() {
        return this.status == StudyStatus.ASSIGNED || this.status == StudyStatus.STARTED;
    }

    public boolean hasStudyWord(StudyWord studyWord) {
        return studyWord != null && Objects.equals(studyWord.getStudy().getId(), this.id);
    }

    public void submit() {
        this.status = StudyStatus.SUBMITTED;
        this.submitDateTime = LocalDateTime.now();
    }

    public boolean isSubmitted() {
        return this.status == StudyStatus.SUBMITTED;
    }

    public boolean isPractice() {
        return this.classification == StudyClassification.PRACTICE;
    }

    public void isCorrectedBy(Teacher teacher) {
        this.teacher = teacher;
    }
}
