package portfolio.sunder.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import portfolio.sunder.infrastructure.jwt.JwtUtils;

import java.io.IOException;
import java.util.regex.Pattern;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // UserQueryRepository 로 직접 조회해도 괜찮다.
    private final UserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader(AUTHORIZATION);
        if(StringUtils.hasText(authorization) && Pattern.matches("^Bearer .*", authorization)) {
            String token = authorization.replaceAll("^Bearer( )*", "");
            if (SecurityContextHolder.getContext().getAuthentication() == null && jwtUtils.hasInvalidStatus(token).isEmpty()) {
                String subject = jwtUtils.extractSubject(token);
                if (StringUtils.hasText(subject)) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(subject);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
