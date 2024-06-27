package psam.portfolio.sunder.english.global.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import psam.portfolio.sunder.english.global.security.userdetails.UserDetailsServiceJwt;
import psam.portfolio.sunder.english.infrastructure.jwt.JwtClaim;
import psam.portfolio.sunder.english.infrastructure.jwt.JwtUtils;
import psam.portfolio.sunder.english.infrastructure.clientinfo.ClientInfoHolder;

import java.io.IOException;
import java.util.regex.Pattern;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserDetailsServiceJwt userDetailsService;
    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader(AUTHORIZATION);
        if(StringUtils.hasText(authorization) && Pattern.matches("^Bearer .*", authorization)) {
            String token = authorization.replaceFirst("^Bearer ", "");
            if (SecurityContextHolder.getContext().getAuthentication() == null && jwtUtils.hasInvalidStatus(token).isEmpty()) {

                // 사용 가능한 토큰이라면 UserDetails 를 생성하고 SecurityContextHolder 에 저장한다.
                UserDetails userDetails = userDetailsService.loadUserByJwt(token);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }
}
