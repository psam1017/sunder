package psam.portfolio.sunder.english.domain.academy.model.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;
import psam.portfolio.sunder.english.domain.academy.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.domain.book.model.entity.Book;
import psam.portfolio.sunder.english.domain.student.model.entity.Student;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.domain.user.model.entity.User;
import psam.portfolio.sunder.english.global.jpa.audit.TimeEntity;
import psam.portfolio.sunder.english.global.jpa.embeddable.Address;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Table(name = "academy_shares")
@Entity
public class AcademyShare extends TimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sharing_academy_id")
    private Academy sharingAcademy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shared_academy_id")
    private Academy sharedAcademy;

    @Builder
    protected AcademyShare(Academy sharingAcademy, Academy sharedAcademy) {
        this.sharingAcademy = sharingAcademy;
        this.sharedAcademy = sharedAcademy;
    }
}
