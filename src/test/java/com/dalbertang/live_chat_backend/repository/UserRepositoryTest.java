package com.dalbertang.live_chat_backend.repository;

import com.dalbertang.live_chat_backend.model.User;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataR2dbcTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("1", "test@example.com", "Test User", Set.of("ROLE_USER"));
        userRepository.deleteAll().block();
    }


    @Test
    void findByEmail() {
        userRepository.insertUser(testUser.getId(), testUser.getEmail(), testUser.getName(), testUser.getRoles().toArray(new String[0])).block();

        Mono<User> userMono = userRepository.findByEmail("test@example.com");

        StepVerifier.create(userMono)
            .expectNextMatches(user -> user.getEmail().equals("test@example.com"))
            .verifyComplete();
    }

    @Test
    void insertUser() {
        Mono<User> userMono = userRepository.insertUser("2", "new@example.com", "New User", new String[]{"ROLE_USER"});

        StepVerifier.create(userMono)
            .expectNextMatches(user -> user.getEmail().equals("new@example.com"))
            .verifyComplete();
    }
}
