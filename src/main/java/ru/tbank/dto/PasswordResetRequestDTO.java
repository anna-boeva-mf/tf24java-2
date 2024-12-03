package ru.tbank.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetRequestDTO {
    private String username;
    private String newPassword;
    private String confirmPassword;
    private String verificationCode;// For 2FA (placeholder)
}
