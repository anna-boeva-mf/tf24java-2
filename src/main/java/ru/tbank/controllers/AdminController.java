package ru.tbank.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tbank.dto.RoleCreationDTO;
import ru.tbank.dto.RoleRequestDTO;
import ru.tbank.exception.EntityAlreadyExistsException;
import ru.tbank.service.UserDetailsServiceImpl;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final UserDetailsServiceImpl userDetailsService;

    public AdminController(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/role/add")
    public ResponseEntity<String> addRole(@RequestBody RoleRequestDTO request) {
        try {
            userDetailsService.addRoleToUser(request.getUsername(), request.getRoleName());
            return ResponseEntity.status(HttpStatus.OK).body("Role added successfully.");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/role/remove")
    public ResponseEntity<String> removeRole(@RequestBody RoleRequestDTO request) {
        try {
            userDetailsService.removeRoleFromUser(request.getUsername(), request.getRoleName());
            return ResponseEntity.ok("Role removed successfully.");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/role/new")
    public ResponseEntity<String> addNewRole(@RequestBody RoleCreationDTO roleCreationDTO) {
        try {
            userDetailsService.addNewRole(roleCreationDTO.getRoleName());
            return ResponseEntity.status(HttpStatus.CREATED).body("New role added successfully.");
        } catch (EntityAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding new role.");
        }
    }
}
