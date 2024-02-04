package psam.portfolio.sunder.english.web.student.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import psam.portfolio.sunder.english.global.jpa.embeddable.Address;
import psam.portfolio.sunder.english.web.student.model.embeddable.Parent;
import psam.portfolio.sunder.english.web.student.model.embeddable.School;
import psam.portfolio.sunder.english.web.teacher.model.entity.Academy;
import psam.portfolio.sunder.english.web.user.enumeration.UserStatus;
import psam.portfolio.sunder.english.web.user.model.entity.User;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("STUDENT")
@Entity
public class Student extends User {

    // 선생과 학생은 다대다 관계이다. 이들은 이미 Academy 를 통해 서로 연관관계를 가지고 있다.

    // 출결번호. 한 Academy 안에서는 unique 해야 한다.
    private String attendanceId;

    // 학생에 대한 특이사항 메모
    @Column(columnDefinition = "TEXT")
    private String note;

    @Embedded
    private School school;

    @Embedded
    private Parent parent;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "academy_uuid")
    private Academy academy;

    @Builder
    public Student(String loginId, String loginPw, String name, String email, boolean emailVerified, String phone, Address address, UserStatus status, Academy academy, String attendanceId, String note, School school, Parent parent) {
        super(loginId, loginPw, name, email, emailVerified, phone, address, status);
        this.attendanceId = attendanceId;
        this.note = note;
        this.school = school;
        this.parent = parent;
        this.academy = academy;
    }

}
