package spring_ocr.pdfscanner.Entities;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import spring_ocr.pdfscanner.Utilities.Enums.UserRoleEnum;

@Entity
@Table(name="users")
@NoArgsConstructor
@Getter
@Setter
public class AppUser {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Size(min=4, max=255, message="Minimum length: 4 characters")
    @Column(unique=true, nullable=false)
    private String username;

    @Column(unique=true, nullable=false)
    private String email;

    @Size(min=8, message="Minimum length: 8 characters")
    private String ownHashedPassword;

    @ElementCollection(fetch= FetchType.EAGER)
    List<UserRoleEnum> appUserRoles;
}
