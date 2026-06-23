package spring_ocr.pdfscanner.Security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    
    private final AppUserDetails userDetails;

    @Value("hz")
    private String secretKey;
    @Value("hz2")
    private Long validity;

    private SecretKey signingKey;

    @PostConstruct
    protected void init() {
        try {
        byte[] keyBytes = MessageDigest.getInstance("SHA-256").digest(secretKey.getBytes(StandardCharsets.UTF_8));
        signingKey = Keys.hmacShaKeyFor(keyBytes);
        } catch (NoSuchAlgorithmException e) {
        throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    public String createToken(String username, List<AppUserRole> appUserRoles) {
        List<String> roleNames = appUserRoles.stream().map(AppUserRole::name).collect(Collectors.toList());
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);
        return Jwts.builder()
            .subject(username)
            .claim("auth", roleNames)
            .issuedAt(now)
            .expiration(validity)
            .signWith(signingKey)
            .compact();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = myUserDetails.loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsername(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(signingKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
        return claims.getSubject();
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
        return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
        Jwts.parser()
            .verifyWith(signingKey)
            .build()
            .parseSignedClaims(token);
        return true;
        } catch (JwtException | IllegalArgumentException e) {
        log.debug("Invalid JWT token", e);
        throw new CustomException("Expired or invalid JWT token", HttpStatus.UNAUTHORIZED);
        }
    }
}
