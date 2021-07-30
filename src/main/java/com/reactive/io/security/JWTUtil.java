package com.reactive.io.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.reactive.io.util.ListUtils.toSingleton;

@Service
public class JWTUtil {

    @Value("${jjwt.secret}")
    private String secret;

    @Value("${token.expiration.in.hour}")
    private int expirationTimeInHour;

    private Key key;

    public static final String KEY_ROLE = "role";

    @PostConstruct
    public void init(){
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public Mono<Claims> getAllClaimsFromToken(String token) {
        return Mono.just(Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody());
    }

    public String generateToken(UserDetails userDetails) {
        String authority = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(toSingleton());
        Map<String, String> claims = new HashMap<>();
        claims.put(KEY_ROLE, authority);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setExpiration(Date.from(Instant.now().plus(Duration.ofHours(expirationTimeInHour))))
                .setIssuedAt(Date.from(Instant.now()))
                .signWith(key)
                .compact();
    }

    public Mono<Boolean> validateToken(String token) {
        return getAllClaimsFromToken(token).map(Claims::getExpiration)
                .map(expiration -> expiration.after(new Date()));
    }
}
