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
@Table(
        name = "user_roles",
        indexes = {
                @Index(columnList = "user_id"),
                @Index(columnList = "role_id")
        }
)
@Entity
public class UserRole {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime assignedDateTime;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Role role;

    @Builder
    public UserRole(User user, Role role) {
        this.assignedDateTime = LocalDateTime.now();
        this.user = user;
        this.role = role;
    }

    public RoleName getRoleName() {
        return this.getRole().getName();
    }
}