package ru.tbank.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tbank.dto.UserRegistrationDTO;
import ru.tbank.exception.RegistrationException;
import ru.tbank.service.RegistrationService;

@Slf4j
@RestController
@RequestMapping({"/api/v1/register"})
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping
    public ResponseEntity<String> register(@RequestBody UserRegistrationDTO userRegistrationDto) {
        try {
            registrationService.registerUser(userRegistrationDto);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (RegistrationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
