package com.reactive.io.service.impl;

import com.reactive.io.entity.dto.UserDto;
import com.reactive.io.entity.mapper.UserMapper;
import com.reactive.io.errors.exception.UserNotFoundException;
import com.reactive.io.respository.UserRepository;
import com.reactive.io.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Log4j2
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Mono<UserDto> getUser(String email) {
        return userRepository.findUserByEmail(email)
                .map(UserMapper.INSTANCE::toDTO)
                .switchIfEmpty(Mono.error(UserNotFoundException::new));
    }
}
