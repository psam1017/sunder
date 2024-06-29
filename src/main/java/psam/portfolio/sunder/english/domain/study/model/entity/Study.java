package psam.portfolio.sunder.english.domain.study.model.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import psam.portfolio.sunder.english.domain.student.model.entity.Student;
import psam.portfolio.sunder.english.domain.study.model.embeddable.StudyRange;
import psam.portfolio.sunder.english.domain.study.model.enumeration.StudyClassification;
import psam.portfolio.sunder.english.domain.study.model.enumeration.StudyStatus;
import psam.portfolio.sunder.english.domain.study.model.enumeration.StudyTarget;
import psam.portfolio.sunder.english.domain.study.model.enumeration.StudyType;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.global.jpa.audit.TimeEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@SQLRestriction(value = "status != 'DELETED'")
@Table(
        name = "studies",
        indexes = {
                @Index(columnList = "student_id"),
                @Index(columnList = "teacher_id")
        }
)
@Entity
public class Study extends TimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private Long sequence;

    @Enumerated(EnumType.STRING)
    private StudyStatus status;
    @Enumerated(EnumType.STRING)
    private StudyType type;
    @Enumerated(EnumType.STRING)
    private StudyClassification classification;
    @Enumerated(EnumType.STRING)
    private StudyTarget target;

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
    // 1. 다대일 테이블로 만들어 다룬다.
    // 2. database 에서 직접 인덱스를 생성한다.
    @ElementCollection
    @CollectionTable(
            name = "study_ranges",
            joinColumns = @JoinColumn(name = "study_id")
    )
    private List<StudyRange> studyRanges = new ArrayList<>();

    @OneToMany(mappedBy = "study")
    private List<StudyWord> studyWords = new ArrayList<>();

    @Builder
    public Study(Long sequence, StudyStatus status, StudyType type, StudyClassification classification, StudyTarget target, Student student, Teacher teacher) {
        this.sequence = sequence;
        this.status = status;
        this.type = type;
        this.classification = classification;
        this.target = target;
        this.student = student;
        this.teacher = teacher;
    }
}
