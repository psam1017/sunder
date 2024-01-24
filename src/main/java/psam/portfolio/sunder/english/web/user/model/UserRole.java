package psam.portfolio.sunder.english.web.user.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "user_roles")
public class UserRole {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_uuid")
    private User user;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    private LocalDateTime assignedDateTime;

    @Builder
    public UserRole(User user, Role role) {
        this.user = user;
        this.role = role;
        this.assignedDateTime = LocalDateTime.now();
    }
}