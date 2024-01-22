package psam.portfolio.sunder.english.web.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import psam.portfolio.sunder.english.global.audit.BaseEntity;
import psam.portfolio.sunder.english.web.role.entity.Role;
import psam.portfolio.sunder.english.web.user.enumeration.UserStatus;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorColumn
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "users")
@Entity
public abstract class User extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @Column(nullable = false, length = 20, unique = true)
    private String loginId;

    @Column(nullable = false, length = 20)
    private String loginPw;

    private LocalDateTime lastPasswordChangeDate;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Role> roles;

    @Builder
    public User(String loginId, String loginPw, String name, UserStatus status, Set<Role> roles) {
        this.loginId = loginId;
        this.loginPw = loginPw;
        this.name = name;
        this.status = status;
        this.roles = roles;
        this.lastPasswordChangeDate = LocalDateTime.now();
    }

    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }

    public boolean isTrial() {
        return this.status == UserStatus.TRIAL;
    }

    public boolean isTrialEnd() {
        return this.status == UserStatus.TRIAL_END;
    }

    public boolean isDormant() {
        return this.status == UserStatus.DORMANT;
    }

    public boolean isForbidden() {
        return this.status == UserStatus.FORBIDDEN;
    }

    public boolean isWithdrawn() {
        return this.status == UserStatus.WITHDRAWN;
    }

    public void activate() {
        this.status = UserStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = UserStatus.DORMANT;
    }

    public void forbid() {
        this.status = UserStatus.FORBIDDEN;
    }

    public void withdraw() {
        this.status = UserStatus.WITHDRAWN;
    }

    public void trialEnd() {
        this.status = UserStatus.TRIAL_END;
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    public boolean hasRole(Role role) {
        return this.roles.contains(role);
    }
}
