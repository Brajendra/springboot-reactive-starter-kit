package com.reactive.io.controller.v1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/health")
public class HealthController {

    private final String UP = "UP";

    @GetMapping
    public Mono<String> check() {
        return Mono.just(UP);
    }
}
