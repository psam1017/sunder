package psam.portfolio.sunder.english.global.security.userdetails;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import psam.portfolio.sunder.english.domain.user.model.entity.User;
import psam.portfolio.sunder.english.domain.user.exception.NoSuchUserException;
import psam.portfolio.sunder.english.domain.user.repository.UserQueryRepository;

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
