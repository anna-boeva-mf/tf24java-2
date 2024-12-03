package ru.tbank.configuration;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class JwtTokenUtil {

    private static final long ACCESS_TOKEN_EXPIRATION_TIME_MINUTES = 10;
    private static final long REFRESH_TOKEN_EXPIRATION_TIME_DAYS = 30;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    private final SecretKey secretKey;

    @Autowired
    public JwtTokenUtil(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.secretKeyFor(signatureAlgorithm);
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
                .signWith(secretKey, signatureAlgorithm)
                .compact();
    }
}
