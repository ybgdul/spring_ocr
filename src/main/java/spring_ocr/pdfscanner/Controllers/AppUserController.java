package spring_ocr.pdfscanner.Controllers;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.Table;
import lombok.RequiredArgsConstructor;
import spring_ocr.pdfscanner.Services.AppUserService;

@RestController
@Table(name="users")
@RequestMapping("/users")
@RequiredArgsConstructor
@ControllerAdvice
public class AppUserController {
    
    private final AppUserService userService;
    private final ModelMapper modelMapper;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> globalHandler(Exception e) { 
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage()); 
    }

    //gotta add methods 
    

}
