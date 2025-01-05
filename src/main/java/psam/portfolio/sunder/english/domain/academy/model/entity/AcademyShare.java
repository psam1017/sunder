package psam.portfolio.sunder.english.domain.academy.model.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import psam.portfolio.sunder.english.global.jpa.audit.TimeEntity;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Table(
        name = "academy_shares",
        indexes = {
                @Index(columnList = "sharing_academy_id"),
                @Index(columnList = "shared_academy_id")
        }
)
@Entity
public class AcademyShare extends TimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "sharing_academy_id",
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    private Academy sharingAcademy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "shared_academy_id",
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    private Academy sharedAcademy;

    @Builder
    protected AcademyShare(Academy sharingAcademy, Academy sharedAcademy) {
        this.sharingAcademy = sharingAcademy;
        this.sharedAcademy = sharedAcademy;
    }
}
