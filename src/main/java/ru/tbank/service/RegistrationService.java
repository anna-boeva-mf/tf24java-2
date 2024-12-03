package ru.tbank.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.tbank.db_repository.RoleRepository;
import ru.tbank.db_repository.UserRepository;
import ru.tbank.entities.Role;
import ru.tbank.entities.User;
import ru.tbank.exception.BadRequestException;
import ru.tbank.exception.InternalServerErrorException;
import ru.tbank.exception.RegistrationException;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RegistrationService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RoleRepository roleRepository;

    @Autowired
    public RegistrationService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.roleRepository = roleRepository;
    }

    public boolean userExists(String username) {
        return userRepository.findByUsername(username) != null;
    }

    @Transactional
    public User  registerUser(User user, List<String> roleNames) throws RegistrationException {
        log.info("Регистрация пользователя");
        if (!(StringUtils.hasText(user.getUsername()) && StringUtils.hasText(user.getPassword()))) {
            log.error("Username or password cannot be empty");
            throw new RegistrationException("Username or password cannot be empty");
        }
        String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        Set<Role> roles = roleNames.stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseGet(() -> {
                            Role newRole = new Role();
                            newRole.setName(roleName);
                            return roleRepository.save(newRole);
                        }))
                .collect(Collectors.toSet());
        try {
            user.setRoles(roles);
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
