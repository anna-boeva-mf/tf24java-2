package ru.tbank.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
public class AuthenticationRequest {
    private String username;
    private String password;

    public AuthenticationRequest() {} // Default constructor (needed for some frameworks)

    public AuthenticationRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

}