package ru.tbank.entities;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class ResetPassRequest {
    private String token;
    private String newPassword;
    private String confirmPassword;
    private String verificationCode;
}
