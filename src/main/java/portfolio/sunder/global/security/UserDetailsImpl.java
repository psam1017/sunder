package portfolio.sunder.global.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import portfolio.sunder.domain.user.entity.User;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authoritySet = new HashSet<>();
        user.getRoles().forEach(r -> authoritySet.add(r::name));
        return authoritySet;
    }

    @Override
    public String getUsername() {
        return user.getUid();
    }

    @Override
    public String getPassword() {
        return user.getUpw();
    }

    // 계정 활성화 여부. 예를 들어 약관 위반에 의한 정지, 탈퇴 후 n 개월 이내(정보 보관 기간), 1년 간 사용하지 않은 휴면 계정 등.
    @Override
    public boolean isEnabled() {
        return user.isActive() || user.isTrial();
    }

    // 계정의 기간 만료 여부. 예를 들어 n 개월 분의 사용권을 얻은 계정
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정 잠금 여부. 예를 들어 로그인을 5회 이상 실패.
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 비밀번호 만료 여부. 예를 들어 비밀번호를 마지막으로 변경한 지 3개월이 지난 경우.
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
