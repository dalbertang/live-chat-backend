package com.dalbertang.live_chat_backend.controller;

import com.dalbertang.live_chat_backend.model.MessageContent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Component
public class ChatWebSocketHandler implements WebSocketHandler {

    private final ConcurrentHashMap<String, CopyOnWriteArrayList<WebSocketSession>> sessions = new ConcurrentHashMap<>();
    private final Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        String user = session.getId();
        sessions.computeIfAbsent("chat", k -> new CopyOnWriteArrayList<>()).add(session);

        Flux<String> receiveFlux = session.receive()
            .map(msg -> {
                String content = msg.getPayloadAsText();
                ObjectMapper objectMapper = new ObjectMapper();
                MessageContent messageContent = new MessageContent(user, content, Instant.now().toString());
                try {
                    String message = objectMapper.writeValueAsString(messageContent);
                    return message;
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            })
            .doOnNext(sink::tryEmitNext);

        return session.send(receiveFlux.map(session::textMessage))
            .doFinally(signalType -> {
                sessions.get("chat").remove(session);
            });
    }
}
