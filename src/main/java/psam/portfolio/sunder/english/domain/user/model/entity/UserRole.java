package psam.portfolio.sunder.english.domain.user.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import psam.portfolio.sunder.english.domain.user.enumeration.RoleName;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_roles",
        indexes = @Index(columnList = "user_uuid")
)
@Entity
public class UserRole {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private RoleName roleName;
    private LocalDateTime assignedDateTime;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_uuid", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @Builder
    public UserRole(User user, RoleName roleName) {
        this.user = user;
        this.roleName = roleName;
        this.assignedDateTime = LocalDateTime.now();
    }
}