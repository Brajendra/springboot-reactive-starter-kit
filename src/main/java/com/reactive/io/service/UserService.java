package com.reactive.io.service;

import com.reactive.io.entity.dto.UserDto;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<UserDto> getUser(String email);
}
