package com.dalbertang.live_chat_backend.controller;

import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/user")
    public Mono<Map<String, Object>> getUser() {
        return ReactiveSecurityContextHolder.getContext()
            .map(securityContext -> securityContext.getAuthentication())
            .cast(OAuth2AuthenticationToken.class)
            .flatMap(this::extractUserInfo)
            .switchIfEmpty(Mono.empty());
    }

    private Mono<Map<String, Object>> extractUserInfo(Authentication authentication) {
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            OAuth2User principal = oauthToken.getPrincipal();

            if (principal instanceof OidcUser) {
                OidcUser oidcUser = (OidcUser) oauthToken.getPrincipal();
                if (oidcUser != null) {
                    return Mono.just(Map.of(
                        "id", oidcUser.getSubject(),
                        "name", oidcUser.getFullName(),
                        "email", oidcUser.getEmail(),
                        "picture", oidcUser.getPicture()
                    ));
                }
            } else if (principal instanceof DefaultOAuth2User) {
                DefaultOAuth2User oauth2User = (DefaultOAuth2User) principal;
                return Mono.just(Map.of(
                    "id", oauth2User.getName(),
                    "name", oauth2User.getAttribute("name"),
                    "email", oauth2User.getAttribute("email"),
                    "picture", oauth2User.getAttribute("picture")
                ));
            }
        }
        return Mono.empty();
    }
}
