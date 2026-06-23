package spring_ocr.pdfscanner.Security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import spring_ocr.pdfscanner.Entities.AppUser;
import spring_ocr.pdfscanner.Repositories.AppUserRepo;

@Service
@RequiredArgsConstructor
public class AppUserDetails implements UserDetailsService {

    private final AppUserRepo userRepo;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        final AppUser appUser = userRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User '" + username + "' not found"));

        return User.withUsername(username).password(appUser.getOwnHashedPassword()).authorities("USER").accountExpired(false).accountLocked(false).credentialsExpired(false).disabled(false).build();
    }
    
}
