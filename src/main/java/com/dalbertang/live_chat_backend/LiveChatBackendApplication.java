package com.dalbertang.live_chat_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

@SpringBootApplication
public class LiveChatBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(LiveChatBackendApplication.class, args);
	}

}
