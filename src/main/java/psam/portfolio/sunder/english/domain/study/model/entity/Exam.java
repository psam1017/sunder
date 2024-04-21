package psam.portfolio.sunder.english.domain.study.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.study.model.enumeration.ExamType;
import psam.portfolio.sunder.english.domain.student.model.entity.Student;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("EXAM")
@PrimaryKeyJoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
@Table(
        name = "exam"
)
@Entity
public class Exam extends Study {

    @Enumerated(EnumType.STRING)
    private ExamType examType;

    private String graderName;

    @OneToMany(mappedBy = "exam")
    private List<ExamWord> examWords = new ArrayList<>();

    @Builder
    public Exam(String publisher, String bookName, String chapter, String subject, Student student, Academy academy, ExamType examType, String graderName) {
        super(publisher, bookName, chapter, subject, student, academy);
        this.examType = examType;
        this.graderName = graderName;
    }

    public void setGraderName(String graderName) {
        this.graderName = graderName;
    }
}
