package com.reactive.io.security;


import com.reactive.io.entity.enums.UserRoles;
import com.reactive.io.entity.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class JWTUtilTest {

    @InjectMocks
    private JWTUtil jwtUtil;

    @BeforeEach
    public void init() {
        ReflectionTestUtils.setField(jwtUtil, "secret", "ThisIsSecretForJWTHS512SignatureAlgorithmThatMUSTHave64ByteLength");
        ReflectionTestUtils.setField(jwtUtil, "expirationTimeInHour", 1);
    }

    @Test
    public void shouldGenerateTokenAndVerifyClaims() {

        String TEST_EMAIL = "test@gmail.com";
        UserDetails userDetails = User.builder()
                .id("id")
                .firstName("firstName")
                .lastName("lastName")
                .phone("phone")
                .email(TEST_EMAIL)
                .role(UserRoles.ADMIN)
                .password("password")
                .active(false)
                .build();

        String token = jwtUtil.generateToken(userDetails);

        Mono<Claims> role = jwtUtil.getAllClaimsFromToken(token);

        StepVerifier.create(role)
                .consumeNextWith(responseClaims -> {
                    assertNotNull(responseClaims);
                    assertNotNull(responseClaims.get("role"));
                    assertEquals(responseClaims.get("role"), "ADMIN");
                })
                .verifyComplete();
    }

    @Test
    public void shouldValidateToken() {
        SecretKey key = Keys.hmacShaKeyFor("ThisIsSecretForJWTHS512SignatureAlgorithmThatMUSTHave64ByteLength".getBytes());
        Map<String, String> claims = new HashMap<>();
        claims.put("role", "ADMIN");
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject("test@gmail.com")
                .setExpiration(Date.from(Instant.now().plus(Duration.ofHours(1))))
                .setIssuedAt(Date.from(Instant.now()))
                .signWith(key)
                .compact();

        Mono<Boolean> result = jwtUtil.validateToken(token);

        StepVerifier.create(result)
                .consumeNextWith(responseClaims -> {
                    assertEquals(responseClaims, true);
                })
                .verifyComplete();
    }
}