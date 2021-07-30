package com.reactive.io.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static com.reactive.io.security.JWTUtil.KEY_ROLE;

@Component
public class AuthenticationManager implements ReactiveAuthenticationManager {

    @Autowired
    private JWTUtil jwtUtil;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {

        String authToken = authentication.getCredentials().toString();
        return Mono.just(authToken)
                .map(token -> jwtUtil.validateToken(token))
                .onErrorResume(e -> Mono.empty())
                .flatMap(isValid -> jwtUtil.getAllClaimsFromToken(authToken))
                .map(claims -> new UsernamePasswordAuthenticationToken(claims.getSubject(), null, Collections.singletonList(new SimpleGrantedAuthority(claims.get(KEY_ROLE).toString()))));
    }
}
