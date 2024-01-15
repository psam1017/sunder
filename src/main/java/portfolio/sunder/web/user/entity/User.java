package portfolio.sunder.web.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import portfolio.sunder.web.user.enumeration.UserRole;
import portfolio.sunder.web.user.enumeration.UserStatus;

import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorColumn
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "users")
@Entity
public class User { // TODO: 2024-01-13 extends TimeEntity

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20, unique = true)
    private String uid;

    @Column(nullable = false, length = 20)
    private String upw;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    private Set<UserRole> roles;

    public User(String uid, String upw, String name, UserStatus status, Set<UserRole> roles) {
        this.uid = uid;
        this.upw = upw;
        this.name = name;
        this.status = status;
        this.roles = roles;
    }

    public boolean isActive() {
        return this.getStatus() == UserStatus.ACTIVE;
    }

    public boolean isTrial() {
        return this.getStatus() == UserStatus.TRIAL;
    }
}
