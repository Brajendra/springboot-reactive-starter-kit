package com.reactive.io.service.impl;

import com.reactive.io.entity.dto.UserDto;
import com.reactive.io.entity.mapper.UserMapper;
import com.reactive.io.errors.exception.WrongCredentialException;
import com.reactive.io.respository.UserRepository;
import com.reactive.io.security.JWTUtil;
import com.reactive.io.service.AuthService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Log4j2
@Service
public class AuthServiceImpl implements AuthService, ReactiveUserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JWTUtil jwtUtil;

    @Override
    public Mono<UserDto> signup(UserDto userDto) {
        return Mono.just(userDto)
                .map(UserMapper.INSTANCE::fromDTO)
                .doOnNext(user -> user.setPassword(passwordEncoder.encode(user.getPassword())))
                .flatMap(userRepository::save)
                .map(UserMapper.INSTANCE::toDTO);
    }


    @Override
    public Mono<String> login(String username, String password) {
        return findByUsername(username)
                .filter(userDetails -> passwordEncoder.matches(password, userDetails.getPassword()))
                .map(userDetails -> jwtUtil.generateToken(userDetails))
                .switchIfEmpty(Mono.error(WrongCredentialException::new));
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(WrongCredentialException::new));
    }
}
