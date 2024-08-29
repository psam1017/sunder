package psam.portfolio.sunder.english.domain.user.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;
import psam.portfolio.sunder.english.global.jpa.audit.BaseEntity;
import psam.portfolio.sunder.english.global.jpa.embeddable.Address;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.JOINED)
@Table(
        name = "users",
        indexes = {
                @Index(columnList = "created_date_time"),
                @Index(columnList = "login_id"),
                @Index(columnList = "email"),
        }
)
@Entity
public abstract class User extends BaseEntity {

    // 탈퇴일도 추가하면 좋을 듯. 하지만 지금은 modifiedDateTime 으로 대체.
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 20)
    private String loginId;

    @Column(nullable = false)
    private String loginPw;

    @Column(nullable = false)
    private String name;

    @Column
    private String email;

    @Column
    private String phone;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    private LocalDateTime lastPasswordChangeDateTime;

    private LocalDateTime passwordChangeAllowedDateTime;

    // 값 타입 컬렉션을 사용하지 않은 이유. ROLE_SECRETARY 등 역할이 추가될 수 있음을 고려하여 정석적인 패턴(SQL_Anti_Patterns.31가지 맛)으로 Role 엔티티를 생성.
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRole> roles = new HashSet<>();

    protected User(String loginId, String loginPw, String name, String email, String phone, Address address, UserStatus status) {
        this.loginId = loginId;
        this.loginPw = loginPw;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.status = status;
        LocalDateTime now = LocalDateTime.now();
        this.lastPasswordChangeDateTime = now;
        this.passwordChangeAllowedDateTime = now;
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

    public void setLastPasswordChangeDateTime(LocalDateTime lastPasswordChangeDateTime) {
        this.lastPasswordChangeDateTime = lastPasswordChangeDateTime;
    }

    public boolean isPasswordExpired() {
        return this.lastPasswordChangeDateTime.plusMonths(3).isBefore(LocalDateTime.now());
    }

    public void setLoginPw(String loginPw) {
        this.loginPw = loginPw;
    }

    public void changeStatus(UserStatus status) {
        this.status = status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setPasswordChangeAllowedDateTime(LocalDateTime passwordChangeAllowedDateTime) {
        this.passwordChangeAllowedDateTime = passwordChangeAllowedDateTime;
    }

    public boolean isPasswordChangeAllowed() {
        return this.passwordChangeAllowedDateTime.isAfter(LocalDateTime.now());
    }
}
