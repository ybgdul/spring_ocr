package spring_ocr.pdfscanner.Utilities.Enums;

import org.springframework.security.core.GrantedAuthority;

public enum UserRoleEnum implements GrantedAuthority {
    USER,
    ADMIN;

    @Override
    public String getAuthority() { 
        return name();
    }
}
