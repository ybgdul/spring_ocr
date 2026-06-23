package spring_ocr.pdfscanner.Utilities.Dtos;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import spring_ocr.pdfscanner.Utilities.Enums.UserRoleEnum;

@Data
@NoArgsConstructor
public class UserRequestDTO {
    
    @NotBlank
    @Size(min = 4, max = 255, message = "Minimum username length: 4 characters")
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, message = "Minimum password length: 8 characters")
    private String password;

    private List<UserRoleEnum> appUserRoles;
}
