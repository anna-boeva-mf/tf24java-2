package ru.tbank.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.tbank.db_repository.UserRepository;
import ru.tbank.entities.AuthenticationRequest;
import ru.tbank.entities.AuthenticationResponse;
import ru.tbank.entities.User;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    @Autowired
    private UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    private static final long ACCESS_TOKEN_EXPIRATION_TIME_MINUTES = 10;
    private static final long REFRESH_TOKEN_EXPIRATION_TIME_DAYS = 30;
    @Value("${spring.security.jwt.private-key}")
    private String privateKey;

    public AuthenticationResponse login(AuthenticationRequest request, boolean rememberMe) {
        log.info("Логин пользователя");
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = generateAccessToken(userDetails);
        String refreshToken = rememberMe ? generateRefreshToken(userDetails) : null;
        AuthenticationResponse response = new AuthenticationResponse(accessToken, refreshToken);
        return response;
    }

    public void logout(HttpServletRequest request) {
        log.info("Логаут пользователя");
        SecurityContextHolder.clearContext();
    }

    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(userDetails, ACCESS_TOKEN_EXPIRATION_TIME_MINUTES, TimeUnit.MINUTES);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(userDetails, REFRESH_TOKEN_EXPIRATION_TIME_DAYS, TimeUnit.DAYS);
    }

    private String generateToken(UserDetails userDetails, long expirationTime, TimeUnit timeUnit) {
        long expirationMillis = timeUnit.toMillis(expirationTime);
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(SignatureAlgorithm.HS512, privateKey)
                .compact();
    }

    private Key codedKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(privateKey));
    }

    private Claims getClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(codedKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public User extractUserByToken(String token) {
        Claims claims = getClaims(token);
        String username = claims.get("sub", String.class);
        User user = userRepository.findByUsername(username);
        return user;
    }

    public boolean isTokenValid(String token) {
        log.debug("Проверка ggg {}");
        return getClaims(token).getExpiration().after(new Date());
    }
}
