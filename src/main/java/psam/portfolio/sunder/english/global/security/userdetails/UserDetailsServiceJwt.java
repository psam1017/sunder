package psam.portfolio.sunder.english.global.security.userdetails;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import psam.portfolio.sunder.english.infrastructure.jwt.JwtUtils;

import static psam.portfolio.sunder.english.infrastructure.jwt.JwtClaim.*;

@RequiredArgsConstructor
public class UserDetailsServiceJwt {

    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper;

    /**
     * In this implementation, the <code>UserDetails</code> is retrieved directly from the token.
     * This approach is more efficient than querying the database, as it avoids additional overhead associated with database queries.
     * However, it's important to note that directly extracting data from tokens may be inaccurate compared to querying the database.
     *
     * @param token the token containing user data.
     * @return a fully populated user record (never <code>null</code>)
     * @throws UsernameNotFoundException if the token is invalid
     */
    public UserDetails loadUserByJwt(String token) throws UsernameNotFoundException {
        try {
            Claims claims = jwtUtils.extractAllClaims(token);
            return new UserDetailsJwt(
                    jwtUtils.extractSubject(token),
                    claims.get(PASSWORD.toString(), String.class),
                    claims.get(USER_STATUS.toString(), String.class),
                    objectMapper.readValue(claims.get(ROLE_NAMES.toString(), String.class), new TypeReference<>() {})
            );
        } catch (JwtException | JacksonException e) {
            throw new UsernameNotFoundException(e.getMessage());
        }
    }
}
