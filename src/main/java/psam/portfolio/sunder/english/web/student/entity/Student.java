package psam.portfolio.sunder.english.web.student.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import psam.portfolio.sunder.english.global.entity.embeddable.Address;
import psam.portfolio.sunder.english.web.lesson.entity.Enrollment;
import psam.portfolio.sunder.english.web.teacher.entity.Academy;
import psam.portfolio.sunder.english.web.user.entity.User;
import psam.portfolio.sunder.english.web.user.enumeration.UserStatus;
import psam.portfolio.sunder.english.web.user.entity.UserRole;

import java.util.Set;

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
