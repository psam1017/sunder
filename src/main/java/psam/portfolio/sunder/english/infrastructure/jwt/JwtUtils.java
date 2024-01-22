package psam.portfolio.sunder.english.infrastructure.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

import static psam.portfolio.sunder.english.infrastructure.jwt.JwtStatus.*;

public class JwtUtils {

    private final Key signingKey;

    public JwtUtils(String secretKey) {
        this.signingKey = getSignInKey(secretKey);
    }

    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(String subject, long jwtExpiration) {
        return generateToken(subject, jwtExpiration, new HashMap<>());
    }

    public String generateToken(String subject, long jwtExpiration, Map<String, Object> extraClaims) {
        return buildToken(extraClaims, subject, jwtExpiration);
    }

    public String generateRefreshToken(String subject, long refreshExpiration) {
        return buildToken(new HashMap<>(), subject, refreshExpiration);
    }

    public String generateRefreshToken(String subject, long refreshExpiration, Map<String, Object> extraClaims) {
        return buildToken(extraClaims, subject, refreshExpiration);
    }

    public String generateToken(UUID id, long jwtExpiration) {
        return generateToken(id, jwtExpiration, new HashMap<>());
    }

    public String generateToken(UUID id, long jwtExpiration, Map<String, Object> extraClaims) {
        return buildToken(extraClaims, String.valueOf(id), jwtExpiration);
    }

    public String generateRefreshToken(UUID id, long refreshExpiration) {
        return buildToken(new HashMap<>(), String.valueOf(id), refreshExpiration);
    }

    public String generateRefreshToken(UUID id, long refreshExpiration, Map<String, Object> extraClaims) {
        return buildToken(extraClaims, String.valueOf(id), refreshExpiration);
    }

    private String buildToken(Map<String, Object> extraClaims, String subject, long expiration) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // millisecond
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey(String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Optional<JwtStatus> hasInvalidStatus(String token) {
        if (!StringUtils.hasText(token)) {
            return Optional.of(BLANK);
        }
        try {
            extractSubject(token);
        } catch (ExpiredJwtException e) {
            return Optional.of(EXPIRED);
        } catch (SignatureException e) {
            return Optional.of(ILLEGAL_SIGNATURE);
        } catch (UnsupportedJwtException e) {
            return Optional.of(UNSUPPORTED);
        } catch (MalformedJwtException e) {
            return Optional.of(MALFORMED);
        }
        return Optional.empty();
    }
}
