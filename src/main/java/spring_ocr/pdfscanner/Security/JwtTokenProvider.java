package spring_ocr.pdfscanner.Security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import spring_ocr.pdfscanner.Utilities.Enums.UserRoleEnum;
import spring_ocr.pdfscanner.Utilities.Exceptions.CustomException;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    
    private final AppUserDetails appUserDetails;

    @Value("${secret.key}")
    private String secretKey;
    @Value("${validity}")
    private Long validity;

    private SecretKey signingKey;
    
    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);
    

    @PostConstruct
    protected void init() {
        try {
        byte[] keyBytes = MessageDigest.getInstance("SHA-256").digest(secretKey.getBytes(StandardCharsets.UTF_8));
        signingKey = Keys.hmacShaKeyFor(keyBytes);
        } catch (NoSuchAlgorithmException e) {
        throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    public String createToken(String username, List<UserRoleEnum> appUserRoles) {
        List<String> roleNames = appUserRoles.stream().map(UserRoleEnum::name).collect(Collectors.toList());
        Date now = new Date();
        Date dateValidity = new Date(now.getTime() + validity);
        return Jwts.builder()
            .subject(username)
            .claim("auth", roleNames)
            .issuedAt(now)
            .expiration(dateValidity)
            .signWith(signingKey)
            .compact();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = appUserDetails.loadUserByUsername(getUsername(token));
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
