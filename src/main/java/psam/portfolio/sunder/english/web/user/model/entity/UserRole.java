package psam.portfolio.sunder.english.web.user.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import psam.portfolio.sunder.english.web.user.enumeration.RoleName;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "user_roles")
public class UserRole {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private RoleName roleName;
    private LocalDateTime assignedDateTime;

    @ManyToOne
    @JoinColumn(name = "user_uuid")
    private User user;

    @Builder
    public UserRole(User user, RoleName roleName) {
        this.user = user;
        this.roleName = roleName;
        this.assignedDateTime = LocalDateTime.now();
    }
}