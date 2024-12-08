package ru.tbank.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.tbank.db_repository.RoleRepository;
import ru.tbank.db_repository.UserRepository;
import ru.tbank.entities.ResetPassRequest;
import ru.tbank.entities.Role;
import ru.tbank.entities.User;
import ru.tbank.exception.BadRequestException;
import ru.tbank.exception.EntityAlreadyExistsException;
import ru.tbank.exception.EntityNotFoundException;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            log.error("Пользователь не найден");
            throw new EntityNotFoundException("User not found: " + username);
        }
        return user;
    }

    public void resetPassword(ResetPassRequest resetPassRequest, User user) {
        log.info("Смена пароля пользователя");
        if (!"0000".equals(resetPassRequest.getVerificationCode())) {
            log.error("Неверный код верификации");
            throw new BadRequestException("Invalid verification code");
        }
        if (!resetPassRequest.getNewPassword().equals(resetPassRequest.getConfirmPassword())){
            log.error("Новый пароль и пароль для подтверждения не совпадают");
            throw new BadRequestException("Confirmation of new password failed");
        }
        user.setPassword(passwordEncoder.encode(resetPassRequest.getNewPassword()));
        userRepository.save(user);
    }

    public void addRoleToUser(String username, String roleName) {
        log.info("Добавление роли пользователю");
        User user = userRepository.findByUsername(username);
        if (user == null) {
            log.error("Пользователь не найден");
            throw new EntityNotFoundException("User not found: " + username);
        }
        Role role = roleRepository.findByName(roleName).orElseThrow(() -> new EntityNotFoundException("Role not found: " + roleName));
        user.getRoles().add(role);
        userRepository.save(user);
    }

    public void removeRoleFromUser(String username, String roleName) {
        log.info("Удаление роли у пользователя");
        User user = userRepository.findByUsername(username);
        if (user == null) {
            log.error("Пользователь не найден");
            throw new EntityNotFoundException("User not found: " + username);
        }
        Role role = roleRepository.findByName(roleName).orElseThrow(() -> new EntityNotFoundException("Role not found: " + roleName));
        user.getRoles().remove(role);
        userRepository.save(user);
    }

    public void addNewRole(String roleName) {
        log.info("Создание новой роли");
        Optional<Role> existingRole = roleRepository.findByName(roleName);
        if(existingRole.isPresent()){
            log.error("Роль уже существует");
            throw new EntityAlreadyExistsException("Role with name " + roleName + " already exists.");
        }
        Role newRole = new Role();
        newRole.setName(roleName);
        roleRepository.save(newRole);
    }
}
