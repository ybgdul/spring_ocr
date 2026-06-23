package spring_ocr.pdfscanner.Security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import spring_ocr.pdfscanner.Utilities.Exceptions.CustomException;

public class JwtTokenFilter extends OncePerRequestFilter{
    private static final Logger log = LoggerFactory.getLogger(JwtTokenFilter.class);
    
    private final JwtTokenProvider jwtTokenProvider;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) { 
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String token = jwtTokenProvider.resolveToken(httpServletRequest);
        try {
        if (token != null && jwtTokenProvider.validateToken(token)) {
            Authentication auth = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        } catch (CustomException ex) {
        log.debug("JWT authentication failed: {}", ex.getMessage());
        SecurityContextHolder.clearContext();
        httpServletResponse.sendError(ex.getHttpStatus().value(), ex.getMessage());
        return;
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

}
