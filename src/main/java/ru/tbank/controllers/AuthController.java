package ru.tbank.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.tbank.configuration.JwtTokenUtil;
import ru.tbank.dto.PasswordResetRequestDTO;
import ru.tbank.dto.RoleCreationDTO;
import ru.tbank.dto.RoleRequestDTO;
import ru.tbank.entities.AuthenticationRequest;
import ru.tbank.entities.AuthenticationResponse;
import ru.tbank.exception.EntityAlreadyExistsException;
import ru.tbank.service.AuthenticationService;
import ru.tbank.service.UserDetailsServiceImpl;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationService authenticationService;
    private final UserDetailsServiceImpl userDetailsService;

    public AuthController(JwtTokenUtil jwtTokenUtil, AuthenticationService authenticationService, UserDetailsServiceImpl userDetailsService) {
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

    @PostMapping("/password/reset")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequestDTO request) {
        try {
            if (request.getNewPassword() == null || request.getNewPassword().length() < 8) {
                return ResponseEntity.badRequest().body("New password must be at least 8 characters.");
            }
            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                return ResponseEntity.badRequest().body("Passwords do not match.");
            }

            // 2. Placeholder 2FA (replace with a real 2FA implementation)
            String verificationCode = request.getVerificationCode();
            if (!"0000".equals(verificationCode)) {
                return ResponseEntity.badRequest().body("Invalid verification code.");
            }

            // 3. Reset the password using UserService
            userDetailsService.resetPassword(request.getUsername(), request.getNewPassword());
            return ResponseEntity.ok("Password reset successful.");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.notFound().build(); //User not found
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Password reset failed."); //Catch any other potential exceptions
        }
    }

    @PostMapping("/role/add")
    public ResponseEntity<?> addRole(@RequestBody RoleRequestDTO request) {
        try {
            userDetailsService.addRoleToUser(request.getUsername(), request.getRoleName());
            return ResponseEntity.ok("Role added successfully.");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }catch ( RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/role/remove")
    public ResponseEntity<?> removeRole(@RequestBody RoleRequestDTO request) {
        try {
            userDetailsService.removeRoleFromUser(request.getUsername(), request.getRoleName());
            return ResponseEntity.ok("Role removed successfully.");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/role/new")
    public ResponseEntity<?> addNewRole(@RequestBody RoleCreationDTO roleCreationDTO) {
        try {
            userDetailsService.addNewRole(roleCreationDTO.getRoleName());
            return ResponseEntity.ok("New role added successfully.");
        } catch (EntityAlreadyExistsException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error adding new role.");
        }
    }

}
