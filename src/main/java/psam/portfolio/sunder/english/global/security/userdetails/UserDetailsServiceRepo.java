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
public class UserDetailsServiceRepo implements UserDetailsService {

    private final UserQueryRepository userQueryRepository;

    /**
     * In this implementation, the <code>UserDetails</code> is retrieved directly from the repository.
     * This approach ensures the most up-to-date and accurate user data, as it reflects real-time changes in the database.
     * However, it's important to note that directly querying the database may incur additional overhead compared to extracting data from tokens.
     *
     * Locates the user based on the username. In the actual implementation, the search
     * may possibly be case sensitive, or case insensitive depending on how the
     * implementation instance is configured. In this case, the <code>UserDetails</code>
     * object that comes back may have a username that is of a different case than what
     * was actually requested..
     * @param username the username identifying the user whose data is required.
     * @return a fully populated user record (never <code>null</code>)
     * @throws UsernameNotFoundException if the user could not be found or the user has no GrantedAuthority
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            User findUser = userQueryRepository.getById(UUID.fromString(username));
            return new UserDetailsRepo(findUser);
        } catch (NoSuchUserException e) {
            throw new UsernameNotFoundException(e.getMessage());
        }
    }
}
