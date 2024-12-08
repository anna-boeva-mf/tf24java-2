package ru.tbank.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.tbank.db_repository.RoleRepository;
import ru.tbank.db_repository.UserRepository;
import ru.tbank.dto.UserRegistrationDTO;
import ru.tbank.entities.Role;
import ru.tbank.entities.User;
import ru.tbank.exception.BadRequestException;
import ru.tbank.exception.InternalServerErrorException;
import ru.tbank.exception.RegistrationException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RegistrationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegistrationService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, RoleRepository roleRepository1) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository1;
    }

    public boolean userExists(String username) {
        return userRepository.findByUsername(username) != null;
    }

    @Transactional
    public User registerUser(UserRegistrationDTO userRegistrationDto) throws RegistrationException {
        log.info("Регистрация пользователя");
        User user = new User();
        user.setUsername(userRegistrationDto.getUsername());
        user.setPassword(userRegistrationDto.getPassword());
        if (userExists(user.getUsername())) {
            log.error("Пользователь с таким именем уже существует");
            throw new RegistrationException("User with this username already exists");
        }
        if (!(StringUtils.hasText(user.getUsername()) && StringUtils.hasText(user.getPassword()))) {
            log.error("Имя пользователя и пароль не должны быть пусты");
            throw new RegistrationException("Username or password cannot be empty");
        }
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        List<String> userRoles = new ArrayList<>();
        userRoles.add("USER");
        Set<Role> roles = userRoles.stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseGet(() -> {
                            Role newRole = new Role();
                            newRole.setName(roleName);
                            return roleRepository.save(newRole);
                        }))
                .collect(Collectors.toSet());
        user.setRoles(roles);
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            log.error("Ошибка добавления пользователя в базу");
            throw new RegistrationException(e.getMessage());
        } catch (BadRequestException e) {
            log.error("Ошибка запроса");
            throw new RegistrationException(e.getMessage());
        } catch (InternalServerErrorException e) {
            log.error("Ошибка работы сервиса");
            throw new RegistrationException(e.getMessage());
        }
    }
}
