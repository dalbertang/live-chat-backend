package com.dalbertang.live_chat_backend.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.dalbertang.live_chat_backend.model.User;
import com.dalbertang.live_chat_backend.repository.UserRepository;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SecurityConfigTest {

    @Autowired
    private ApplicationContext context;

    @MockitoBean
    private ExchangeFunction exchangeFunction;

    @MockitoBean
    private UserRepository userRepository;

    private WebTestClient webTestClient;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        when(exchangeFunction.exchange(any())).thenReturn(Mono.just(ClientResponse.create(HttpStatus.OK)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body("{\"sub\":\"1234567890\",\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}")
            .build()));

        when(userRepository.findByEmail(any())).thenReturn(Mono.empty());
        when(userRepository.insertUser(anyString(), anyString(), anyString(), any())).thenReturn(Mono.just(new User()));

        this.webTestClient = WebTestClient.bindToApplicationContext(context)
            .apply(SecurityMockServerConfigurers.springSecurity())
            .configureClient()
            .baseUrl("http://localhost:" + port)
            .build();
    }

    @Test
    void securityWebFilterChain() {
        SecurityWebFilterChain securityWebFilterChain = context.getBean(SecurityWebFilterChain.class);
        assertThat(securityWebFilterChain).isNotNull();
    }

    @Test
    void oidcLogoutSuccessHandler() {
        Object oidcLogoutSuccessHandler = context.getBean("oidcLogoutSuccessHandler");
        assertThat(oidcLogoutSuccessHandler).isNotNull();
    }

    @Test
    void whenAccessSecuredEndpointWithoutAuth_thenUnauthorized() {
        webTestClient.get().uri("/auth/user")
            .exchange()
            .expectStatus().is3xxRedirection();
    }

    @Test
    @WithMockUser
    void whenAccessSecuredEndpointWithAuth_thenOk() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.mockOidcLogin()
                .idToken(builder -> {
                    builder.claim(StandardClaimNames.EMAIL, "some@gmail.com");
                    builder.claim(StandardClaimNames.NAME, "John Doe");
                    builder.claim(StandardClaimNames.SUB, "12312312");
                })
                .authorities(Collections.singletonList(() -> "ROLE_USER")))
            .get().uri("/auth/user")
            .exchange()
            .expectStatus().isOk();
    }
}
