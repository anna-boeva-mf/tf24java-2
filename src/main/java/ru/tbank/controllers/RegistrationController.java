package ru.tbank.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.tbank.dto.UserRegistrationDto;
import ru.tbank.entities.User;
import ru.tbank.exception.BadRequestException;
import ru.tbank.exception.EntityNotFoundException;
import ru.tbank.exception.InternalServerErrorException;
import ru.tbank.service.RegistrationService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping({"/api/v1/register"})
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping
    public ResponseEntity<User> register(@RequestBody UserRegistrationDto userRegistrationDto) {
        User user = new User();
        user.setUsername(userRegistrationDto.getUsername());
        user.setPassword(userRegistrationDto.getPassword());
        List<String> userRoles = new ArrayList<>();
        userRoles.add("USER");

        if (registrationService.userExists(user.getUsername())) {
            throw new BadRequestException("User with this username already exists");
        }

        try {
            registrationService.registerUser(user, userRoles);
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (Exception e) {
            throw new InternalServerErrorException("Error registering user");
        }
    }

    private Map<String, String> getErrors(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return errors;
    }
}
