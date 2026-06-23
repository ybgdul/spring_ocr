package spring_ocr.pdfscanner.Utilities.Dtos;

import java.util.List;

import lombok.Data;
import spring_ocr.pdfscanner.Utilities.Enums.UserRoleEnum;

@Data
public class UserResponseDTO {
    
    private Integer id;
    private String username;
    private String email;
    List<UserRoleEnum> appUserRoles;
}
