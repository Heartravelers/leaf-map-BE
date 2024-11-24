package leafmap.server.global.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider implements InitializingBean  {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_TYPE = "Bearer";

    @Value("${spring.jwt.access-token.expiration-time}")
    private long accessTokenExpirationMs;

    @Value("${spring.jwt.refresh-token.expiration-time}")
    private long refreshTokenExpirationMs;

    @Value("${jwt.secret}")
    private String secretKey;

    private SecretKey key;

    @Override
    public void afterPropertiesSet() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }


    /**
     * Generates an access token with authorities and expiration.
     */
    public String generateAccessToken(Authentication authentication) {
        return createToken(authentication.getName(), "access", accessTokenExpirationMs, getAuthorities(authentication));
    }

    /**
     * Generates a refresh token with expiration only.
     */
    public String generateRefreshToken() {
        return createToken(null, "refresh", refreshTokenExpirationMs, null);
    }

    /**
     * Creates a token with the specified details.
     */
    private String createToken(String subject, String type, long expirationMillis, String authorities) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMillis);

        JwtBuilder builder = Jwts.builder()
                .setExpiration(expiration)
                .signWith(key, SignatureAlgorithm.HS512) // More secure signing algorithm
                .claim("type", type);

        if (subject != null) {
            builder.setSubject(subject);
        }
        if (authorities != null) {
            builder.claim(AUTHORITIES_KEY, authorities);
        }

        return builder.compact();
    }

    /**
     * Extracts authorities from the authentication object.
     */
    private String getAuthorities(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

    /**
     * Retrieves authentication from the JWT access token.
     */
    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new RuntimeException("Token does not contain authority information.");
        }

        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    /**
     * Validates the JWT token.
     */
    public Boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.info("Invalid JWT token: {}", e.getMessage());
        } catch (SecurityException | IllegalArgumentException e) {
            log.info("JWT claims string is empty or signature does not match.");
        }
        return false;
    }

    /**
     * Parses claims from the token and returns expired claims if the token is expired.
     */
    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    /**
     * Resolves the token from request header.
     */
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_TYPE)) {
            return bearerToken.substring(BEARER_TYPE.length()).trim();
        }
        return null;
    }
}
