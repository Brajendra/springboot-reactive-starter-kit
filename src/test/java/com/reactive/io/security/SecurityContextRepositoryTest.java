package com.reactive.io.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
public class SecurityContextRepositoryTest {

    @MockBean
    SecurityContextRepository securityContextRepository;

    @Mock
    private ServerWebExchange serverWebExchange;

    @Mock
    private Authentication authentication;

    @Test
    public void shouldLoad() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer token");
        MockServerHttpRequest request = MockServerHttpRequest.get("/")
                .headers(httpHeaders).build();

        when(serverWebExchange.getRequest()).thenReturn(request);
        when(securityContextRepository.load(any(ServerWebExchange.class))).thenReturn(Mono.just(new SecurityContextImpl(authentication)));

        StepVerifier.create(securityContextRepository.load(serverWebExchange))
                .consumeNextWith(securityContext -> {
                    assertNotNull(securityContext.getAuthentication());
                })
                .verifyComplete();
    }

    @Test
    public void shouldLoadAuthenticationFails() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.AUTHORIZATION, "");
        MockServerHttpRequest request = MockServerHttpRequest.get("/")
                .headers(httpHeaders).build();
        when(serverWebExchange.getRequest()).thenReturn(request);
        when(securityContextRepository.load(any(ServerWebExchange.class))).thenReturn(Mono.empty());
        StepVerifier.create(securityContextRepository.load(serverWebExchange))
                .expectNextCount(0)
                .verifyComplete();
    }
}