package psam.portfolio.sunder.english.web.student.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import psam.portfolio.sunder.english.web.teacher.model.entity.Academy;
import psam.portfolio.sunder.english.web.user.model.entity.User;

import static jakarta.persistence.FetchType.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("STUDENT")
@Entity
public class Student extends User {

    // TODO: 2024-01-22 Embedded 로 변경

    private int grade;

    private int classroom;

    private String school;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "academy_uuid")
    private Academy academy;
}
