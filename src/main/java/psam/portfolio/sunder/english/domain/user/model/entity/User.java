package psam.portfolio.sunder.english.domain.user.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import psam.portfolio.sunder.english.global.jpa.audit.BaseEntity;
import psam.portfolio.sunder.english.global.jpa.embeddable.Address;
import psam.portfolio.sunder.english.domain.user.enumeration.RoleName;
import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;

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

    // 탈퇴일도 추가하면 좋을 듯. 하지만 지금은 modifiedDateTime 으로 대체.

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @Column(nullable = false, length = 20)
    private String loginId;

    @Column(nullable = false)
    private String loginPw;

    @Column(nullable = false)
    private String name;

    @Column
    private String email;

    private boolean emailVerified;

    @Column
    private String phone;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    private LocalDateTime lastPasswordChangeDateTime;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRole> roles;

    protected User(String loginId, String loginPw, String name, String email, boolean emailVerified, String phone, Address address, UserStatus status) {
        this.loginId = loginId;
        this.loginPw = loginPw;
        this.name = name;
        this.email = email;
        this.emailVerified = emailVerified;
        this.phone = phone;
        this.address = address;
        this.status = status;
        this.lastPasswordChangeDateTime = LocalDateTime.now();
    }

    public void verifyEmail(boolean verified) {
        this.emailVerified = verified;
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

    public void startTrial() {
        this.status = UserStatus.TRIAL;
    }

    public boolean isPasswordExpired() {
        return this.lastPasswordChangeDateTime.plusMonths(3).isBefore(LocalDateTime.now());
    }

    public boolean isAdmin() {
        return this.roles.stream().anyMatch(userRole -> userRole.getRoleName() == RoleName.ROLE_ADMIN);
    }
}
