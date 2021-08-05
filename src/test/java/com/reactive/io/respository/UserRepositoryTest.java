package com.reactive.io.respository;

import com.reactive.io.entity.enums.UserRoles;
import com.reactive.io.entity.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataMongoTest
@ExtendWith(SpringExtension.class)
public class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    @Test
    public void shouldSaveSingleUser() {
        User user = User.builder()
                .firstName("firstName")
                .lastName("lastName")
                .phone("phone")
                .email("test@gmail.com")
                .role(UserRoles.ADMIN)
                .password("password")
                .active(false)
                .build();
        Publisher<User> setup = repository.deleteAll().thenMany(repository.save(user));
        StepVerifier
                .create(setup)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void shouldSaveTwoUser() {
        User user = User.builder()
                .firstName("firstName")
                .lastName("lastName")
                .phone("phone")
                .email("test@gmail.com")
                .role(UserRoles.ADMIN)
                .password("password")
                .active(false)
                .build();
        User user1 = User.builder()
                .firstName("firstName")
                .lastName("lastName")
                .phone("phone")
                .email("test1@gmail.com")
                .role(UserRoles.ADMIN)
                .password("password")
                .active(false)
                .build();
        Flux<User> setup = repository.deleteAll().thenMany(Flux.just(user, user1).flatMap(repository::save));
        StepVerifier
                .create(setup)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    public void shouldSaveUser() {
        String TEST_EMAIL = "test@gmail.com";
        User user = User.builder()
                .firstName("firstName")
                .lastName("lastName")
                .phone("phone")
                .email(TEST_EMAIL)
                .role(UserRoles.ADMIN)
                .password("password")
                .active(false)
                .build();
        Publisher<User> setup = repository.deleteAll().then(repository.save(user));
        Mono<User> find = repository.findUserByEmail(TEST_EMAIL);
        Publisher<User> composite = Mono
                .from(setup)
                .then(find);
        StepVerifier
                .create(composite)
                .consumeNextWith(account -> {
                    assertNotNull(account.getId());
                    assertEquals(account.getEmail(), TEST_EMAIL);
                    assertEquals(account.getFirstName(), "firstName");
                    assertEquals(account.getLastName(), "lastName");
                })
                .verifyComplete();
    }

    @Test
    public void shouldFindUserByEmail() {
        String TEST_EMAIL = "test@gmail.com";
        User user = User.builder()
                .firstName("firstName")
                .lastName("lastName")
                .phone("phone")
                .email(TEST_EMAIL)
                .role(UserRoles.ADMIN)
                .password("password")
                .active(false)
                .build();
        Publisher<User> setup = repository.deleteAll().then(repository.save(user));
        Mono<User> find = repository.findUserByEmail(TEST_EMAIL);
        Publisher<User> composite = Mono
                .from(setup)
                .then(find);
        StepVerifier
                .create(composite)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void shouldFindUserByEmailFail() {
        String TEST_EMAIL = "test@gmail.com";
        User user = User.builder()
                .firstName("firstName")
                .lastName("lastName")
                .phone("phone")
                .email(TEST_EMAIL)
                .role(UserRoles.ADMIN)
                .password("password")
                .active(false)
                .build();
        Publisher<User> setup = repository.deleteAll().then(repository.save(user));
        Mono<User> find = repository.findUserByEmail("g@gmail.com");
        Publisher<User> composite = Mono
                .from(setup)
                .then(find);
        StepVerifier
                .create(composite)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    public void shouldFindByEmail() {
        String TEST_EMAIL = "test@gmail.com";
        User user = User.builder()
                .firstName("firstName")
                .lastName("lastName")
                .phone("phone")
                .email(TEST_EMAIL)
                .role(UserRoles.ADMIN)
                .password("password")
                .active(false)
                .build();
        Publisher<User> setup = repository.deleteAll().then(repository.save(user));
        Mono<UserDetails> find = repository.findByEmail(TEST_EMAIL);
        Publisher<UserDetails> composite = Mono
                .from(setup)
                .then(find);
        StepVerifier
                .create(composite)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void shouldFindByEmailFail() {
        String TEST_EMAIL = "test@gmail.com";
        User user = User.builder()
                .firstName("firstName")
                .lastName("lastName")
                .phone("phone")
                .email(TEST_EMAIL)
                .role(UserRoles.ADMIN)
                .password("password")
                .active(false)
                .build();
        Publisher<User> setup = repository.deleteAll().then(repository.save(user));
        Mono<UserDetails> find = repository.findByEmail("g@gmail.com");
        Publisher<UserDetails> composite = Mono
                .from(setup)
                .then(find);
        StepVerifier
                .create(composite)
                .expectNextCount(0)
                .verifyComplete();
    }
}