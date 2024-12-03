package ru.tbank.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.tbank.configuration.JwtTokenUtil;
import ru.tbank.entities.AuthenticationRequest;
import ru.tbank.entities.AuthenticationResponse;
import ru.tbank.service.AuthenticationService;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationService authenticationService;
    private final UserDetailsService userDetailsService;

    public AuthController(JwtTokenUtil jwtTokenUtil, AuthenticationService authenticationService, UserDetailsService userDetailsService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.authenticationService = authenticationService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest request, @RequestParam(required = false) Boolean rememberMe) {

        authenticationService.login(request);

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String accessToken = jwtTokenUtil.generateAccessToken(userDetails);
        String refreshToken = rememberMe != null && rememberMe ? jwtTokenUtil.generateRefreshToken(userDetails) : null;

        Map<String, String> response = new HashMap<>();
        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshToken);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        authenticationService.logout(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
