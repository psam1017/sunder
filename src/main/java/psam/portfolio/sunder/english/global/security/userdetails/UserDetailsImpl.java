package psam.portfolio.sunder.english.global.security.userdetails;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import psam.portfolio.sunder.english.web.user.model.entity.User;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authoritySet = new HashSet<>();
        user.getRoles().forEach(userRole -> authoritySet.add(() -> userRole.getRoleName().toString()));
        return authoritySet;
    }

    @Override
    public String getUsername() {
        return String.valueOf(user.getUuid());
    }

    @Override
    public String getPassword() {
        return user.getLoginPw();
    }

    // 계정 활성화 여부. 예를 들어 약관 위반에 의한 정지
    @Override
    public boolean isEnabled() {
        return user.isActive() || user.isTrial();
    }

    // 계정 만료 여부
    @Override
    public boolean isAccountNonExpired() {
        return !user.isTrialEnd();
    }

    // 인증 정보(credentials) 만료 여부
    @Override
    public boolean isCredentialsNonExpired() {
        return user.isPasswordExpired();
    }

    // 계정 잠김 여부
    @Override
    public boolean isAccountNonLocked() {
        return !user.isForbidden();
    }
}
