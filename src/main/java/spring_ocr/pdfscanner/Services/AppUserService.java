package spring_ocr.pdfscanner.Services;

import javax.naming.AuthenticationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import spring_ocr.pdfscanner.Entities.AppUser;
import spring_ocr.pdfscanner.Repositories.AppUserRepo;
import spring_ocr.pdfscanner.Security.JwtTokenProvider;
import spring_ocr.pdfscanner.Utilities.Exceptions.CustomException;

@Service
@RequiredArgsConstructor
public class AppUserService {
    
    private static final Logger log = LoggerFactory.getLogger(AppUserService.class);

    private final AppUserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    public String signIn(String username, String password) { 
        try { 
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            String token = tokenProvider.createToken(username, userRepo.findByUsername(username).orElseThrow(() -> new AuthenticationException("User doesn't exist")).getAppUserRoles());
            log.info("User has signed in: ", username);
            return token;
        } catch(AuthenticationException e) { 
            throw new CustomException("Invalid username/password supplied", HttpStatus.UNPROCESSABLE_CONTENT);
        }
    }

    public String signUp(AppUser user) { 
        if(!userRepo.existsByUsername(user.getUsername())) {
            user.setOwnHashedPassword(passwordEncoder.encode(user.getOwnHashedPassword()));
            userRepo.save(user);
            log.info("User has been signed up: ", user.getUsername());
            return tokenProvider.createToken(user.getUsername(), user.getAppUserRoles());
        } else throw new CustomException("User already exists: " + user.getUsername(), HttpStatus.UNPROCESSABLE_CONTENT); } 

    public void delete(String username) { 
        log.info("about to delete user: ", username);
        userRepo.deleteByUsername(username);
    }

    public AppUser getMe(HttpServletRequest request) { 
        String token = tokenProvider.resolveToken(request);
        if(token == null) throw new CustomException("Missing or invalid token", HttpStatus.UNAUTHORIZED);
        AppUser user = userRepo.findByUsername(tokenProvider.getUsername(token)).orElseThrow( () -> new CustomException("User not found by given username", HttpStatus.NOT_FOUND));
        return user;
    }

    public AppUser search( String username) { 
        AppUser user = userRepo.findByUsername(username).orElseThrow( () -> new CustomException("User not found by given username", HttpStatus.NOT_FOUND));
        return user;
    }

    public String refresh(String username) { 
        AppUser user = userRepo.findByUsername(username).orElseThrow( () -> new CustomException("User not found by given username", HttpStatus.NOT_FOUND));
        return tokenProvider.createToken(username, user.getAppUserRoles());
    }
    
}
