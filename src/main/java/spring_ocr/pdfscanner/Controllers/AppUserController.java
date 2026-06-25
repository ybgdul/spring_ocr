package spring_ocr.pdfscanner.Controllers;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.Table;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import spring_ocr.pdfscanner.Entities.AppUser;
import spring_ocr.pdfscanner.Services.AppUserService;
import spring_ocr.pdfscanner.Utilities.Dtos.UserRequestDTO;
import spring_ocr.pdfscanner.Utilities.Dtos.UserResponseDTO;

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

    @PostMapping("signin")
    public String logIn(@RequestParam String username, @RequestParam String password) { 
        return userService.signIn(username, password);
    }

    @PostMapping("signup")
    public String signUp(@RequestBody @Valid UserRequestDTO user) {
        return userService.signUp(modelMapper.map(user, AppUser.class));
    }

    @DeleteMapping(value = "/{username}")
    public String delete(@PathVariable String username) { 
        userService.delete(username);
        return username;
    }

    @GetMapping(value = "/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponseDTO search(@PathVariable String username) { 
        return modelMapper.map(userService.search(username), UserResponseDTO.class);
    }

    @GetMapping(value = "/me")
    public UserResponseDTO whoami(HttpServletRequest req) { 
        return modelMapper.map(userService.getMe(req), UserResponseDTO.class);
    }
    
    @GetMapping("/refresh")
    public String refresh(Authentication authentication) { 
        return userService.refresh(authentication.getName());
    }

}
