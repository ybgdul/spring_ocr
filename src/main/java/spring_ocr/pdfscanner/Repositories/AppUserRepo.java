package spring_ocr.pdfscanner.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import spring_ocr.pdfscanner.Entities.AppUser;

public interface AppUserRepo extends JpaRepository<AppUser, Long> {
    
}
