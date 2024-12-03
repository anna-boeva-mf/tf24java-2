package ru.tbank.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.tbank.entities.AuthenticationRequest;
import ru.tbank.entities.AuthenticationResponse;
import ru.tbank.entities.ResetPassRequest;
import ru.tbank.entities.User;
import ru.tbank.service.AuthenticationService;
import ru.tbank.service.UserDetailsServiceImpl;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthenticationService authenticationService;
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    public AuthController(AuthenticationService authenticationService, UserDetailsServiceImpl userDetailsServiceImpl) {
        this.authenticationService = authenticationService;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request, @RequestParam(required = false) Boolean rememberMe) {
        AuthenticationResponse loginResponse = authenticationService.login(request, rememberMe);
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        authenticationService.logout(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPassRequest resetPassRequest) {
        String token = resetPassRequest.getToken();
        User user = authenticationService.extractUserByToken(token);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
        }
        userDetailsServiceImpl.resetPassword(resetPassRequest, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
