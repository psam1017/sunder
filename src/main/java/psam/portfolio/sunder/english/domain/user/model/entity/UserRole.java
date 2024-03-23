package psam.portfolio.sunder.english.domain.user.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import psam.portfolio.sunder.english.domain.user.enumeration.RoleName;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "user_roles",
        indexes = {
                @Index(name = "idx_user_roles_user_uuid", columnList = "user_uuid"),
                @Index(name = "idx_user_roles_role_id", columnList = "role_id")
        }
)
@Entity
public class UserRole {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime assignedDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_uuid", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Role role;

    @Builder
    public UserRole(User user, Role role) {
        this.user = user;
        this.assignedDateTime = LocalDateTime.now();
        this.role = role;
    }

    public RoleName getRoleName() {
        return this.getRole().getName();
    }
}