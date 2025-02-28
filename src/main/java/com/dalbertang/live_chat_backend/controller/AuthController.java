package com.dalbertang.live_chat_backend.controller;

import com.dalbertang.live_chat_backend.model.User;
import com.dalbertang.live_chat_backend.repository.UserRepository;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/user")
    public Mono<User> getUser(OAuth2AuthenticationToken auth) {
        DefaultOAuth2User oauthUser = (DefaultOAuth2User) auth.getPrincipal();

        String id = oauthUser.getAttribute("sub"); // Google ID
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");

        return userRepository.findByEmail(email)
            .switchIfEmpty(Mono.defer(() -> userRepository.insertUser(id, email, name, new String[]{"ROLE_USER"})
        ));
    }
}
