package psam.portfolio.sunder.english.global.security.userdetails;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import psam.portfolio.sunder.english.domain.user.enumeration.RoleName;
import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserDetailsJwt implements UserDetails {

    private final String username;
    private final String password;
    private final UserStatus status;
    private final List<RoleName> roles;

    public UserDetailsJwt(String username, String password, String status, List<RoleName> roles) {
        this.username = username;
        this.password = password;
        this.status = UserStatus.ofNullable(status);
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authoritySet = new HashSet<>();
        roles.forEach(role -> authoritySet.add(role::toString));
        return authoritySet;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    // 계정 활성화 여부
    @Override
    public boolean isEnabled() {
        return this.status == UserStatus.ACTIVE || this.status == UserStatus.TRIAL;
    }

    // 계정 만료 여부
    @Override
    public boolean isAccountNonExpired() {
        return this.status != UserStatus.TRIAL_END;
    }

    // 인증 정보(credentials) 만료 여부
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정 잠김 여부
    @Override
    public boolean isAccountNonLocked() {
        return this.status != UserStatus.FORBIDDEN;
    }
}
