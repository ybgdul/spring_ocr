package spring_ocr.pdfscanner.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import spring_ocr.pdfscanner.Entities.AppUser;

public interface AppUserRepo extends JpaRepository<AppUser, Long> {
    
    public Optional<AppUser> findByUsername(String username);

    public void deleteByUsername(String username);
    
    public boolean existsByUsername(String username);
}
