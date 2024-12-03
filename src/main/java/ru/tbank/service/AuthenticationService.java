package ru.tbank.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.tbank.configuration.JwtTokenUtil;
import ru.tbank.entities.AuthenticationRequest;
import ru.tbank.entities.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    public AuthenticationResponse login(AuthenticationRequest request) {
        log.info(String.valueOf(request));

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = jwtTokenUtil.generateAccessToken(userDetails);

        return new AuthenticationResponse(accessToken);
    }


    public void logout(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
    }
}
