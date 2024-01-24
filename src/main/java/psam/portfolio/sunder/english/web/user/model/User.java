package psam.portfolio.sunder.english.web.user.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import psam.portfolio.sunder.english.global.entity.audit.BaseEntity;
import psam.portfolio.sunder.english.global.entity.embeddable.Address;
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

    @Column(nullable = false)
    private String loginPw;

    @Column(nullable = false)
    private String name;

    private String phone;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    private LocalDateTime lastPasswordChangeDateTime;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRole> roles;

    public User(String loginId, String loginPw, String name, String phone, Address address, UserStatus status, Set<UserRole> roles) {
        this.loginId = loginId;
        this.loginPw = loginPw;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.status = status;
        this.roles = roles;
        this.lastPasswordChangeDateTime = LocalDateTime.now();
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

    public boolean isForbidden() {
        return this.status == UserStatus.FORBIDDEN;
    }

    public boolean isWithdrawn() {
        return this.status == UserStatus.WITHDRAWN;
    }

    public void activate() {
        this.status = UserStatus.ACTIVE;
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
        UserRole userRole = UserRole.builder()
                .user(this)
                .role(role)
                .build();
        this.roles.add(userRole);
    }

    public void removeRole(Role role) {
        this.roles.removeIf(ur -> ur.getRole().equals(role));
    }

    public boolean hasRole(Role role) {
        return this.roles.stream().anyMatch(ur -> ur.getRole().equals(role));
    }
}
