package portfolio.sunder.global.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import portfolio.sunder.domain.user.entity.User;
import portfolio.sunder.domain.user.exception.NoSuchUserException;
import portfolio.sunder.domain.user.repository.UserQueryRepository;

@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserQueryRepository userQueryRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            User findUser = userQueryRepository.getById(Long.parseLong(username));
            return new UserDetailsImpl(findUser);
        } catch (NoSuchUserException e) {
            throw new UsernameNotFoundException(e.getMessage());
        }
    }
}
