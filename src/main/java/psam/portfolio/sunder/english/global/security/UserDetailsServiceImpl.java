package psam.portfolio.sunder.english.global.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import psam.portfolio.sunder.english.web.user.entity.User;
import psam.portfolio.sunder.english.web.user.exception.NoSuchUserException;
import psam.portfolio.sunder.english.web.user.repository.UserQueryRepository;

import java.util.UUID;

@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserQueryRepository userQueryRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            User findUser = userQueryRepository.getById(UUID.fromString(username));
            return new UserDetailsImpl(findUser);
        } catch (NoSuchUserException e) {
            throw new UsernameNotFoundException(e.getMessage());
        }
    }
}
