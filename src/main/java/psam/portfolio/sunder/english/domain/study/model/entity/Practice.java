package psam.portfolio.sunder.english.domain.study.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.student.model.entity.Student;
import psam.portfolio.sunder.english.domain.study.model.enumeration.PracticeType;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("PRACTICE")
@Entity
public class Practice extends Study {

    @Enumerated(EnumType.STRING)
    private PracticeType practiceType;

    @OneToMany(mappedBy = "practice")
    private List<PracticeWord> practiceWords = new ArrayList<>();

    @Builder
    public Practice(String publisher, String bookName, String chapter, String subject, Student student, Academy academy, PracticeType practiceType) {
        super(publisher, bookName, chapter, subject, student, academy);
        this.practiceType = practiceType;
    }
}
