package ru.tbank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.tbank.db_repository.RoleRepository;
import ru.tbank.db_repository.UserRepository;
import ru.tbank.entities.Role;
import ru.tbank.entities.User;
import ru.tbank.exception.EntityAlreadyExistsException;
import ru.tbank.exception.EntityNotFoundException;

import java.util.Optional;

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
            throw new EntityNotFoundException("User not found: " + username);
        }
        return user;
    }

    public void resetPassword(String username, String newPassword) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new EntityNotFoundException("User not found: " + username);
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void addRoleToUser(String username, String roleName) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new EntityNotFoundException("User not found: " + username);
        }
        Role role = roleRepository.findByName(roleName).orElseThrow(() -> new EntityNotFoundException("Role not found: " + roleName));
        user.getRoles().add(role);
        userRepository.save(user);
    }

    public void removeRoleFromUser(String username, String roleName) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new EntityNotFoundException("User not found: " + username);
        }
        Role role = roleRepository.findByName(roleName).orElseThrow(() -> new EntityNotFoundException("Role not found: " + roleName));
        user.getRoles().remove(role);
        userRepository.save(user);
    }

    public void addNewRole(String roleName) {
        //1. Check if the role already exists
        Optional<Role> existingRole = roleRepository.findByName(roleName);
        if(existingRole.isPresent()){
            throw new EntityAlreadyExistsException("Role with name '" + roleName + "' already exists.");
        }

        //2. Create a new role and save it
        Role newRole = new Role();
        newRole.setName(roleName);
        roleRepository.save(newRole);
    }

}
