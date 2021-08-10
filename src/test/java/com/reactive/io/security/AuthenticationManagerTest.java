package com.reactive.io.security;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.exceptions.base.MockitoException;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;

import static com.reactive.io.security.JWTUtil.KEY_ROLE;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class AuthenticationManagerTest {

    @MockBean
    AuthenticationManager authenticationManager;

    @Mock
    JWTUtil jwtUtil;

    @Test
    public void shouldAuthenticate() {

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken("subject", null,
                Collections.singletonList(new SimpleGrantedAuthority(KEY_ROLE)));

        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(Mono.just(usernamePasswordAuthenticationToken));

        StepVerifier.create(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("token", "token")))
                .consumeNextWith(testResult -> {
                    assertTrue(testResult.isAuthenticated());
                })
                .verifyComplete();
    }

    @Test
    public void shouldAuthenticateFail() {

        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(Mono.empty());
        when(jwtUtil.validateToken(anyString())).thenThrow(new MockitoException(""));
        StepVerifier.create(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("token", "token")))
                .expectNextCount(0)
                .verifyComplete();
    }

}