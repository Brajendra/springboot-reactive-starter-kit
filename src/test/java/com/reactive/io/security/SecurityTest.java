package com.reactive.io.security;

import com.reactive.io.ReactiveApplication;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.Base64Utils;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = ReactiveApplication.class)
public class SecurityTest {

    @Autowired
    private WebTestClient rest;

    @Test
    public void shouldUnAuthorizeUser() {
        this.rest.get()
                .uri("/user")
                .header("Authorization", "Bearer test")
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    public void shouldGetUser() {
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
        rest
                .get().uri("/user")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus()
                .isForbidden();
    }
}
