package com.dalbertang.live_chat_backend.controller;

import com.dalbertang.live_chat_backend.model.MessageDTO;
import com.dalbertang.live_chat_backend.model.MessageEntity;
import com.dalbertang.live_chat_backend.service.MessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.Many;
import reactor.core.scheduler.Schedulers;

@Component
public class ChatWebSocketHandler implements WebSocketHandler {
    private final MessageService messageService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Sinks.Many<String> chatSink = Sinks.many().multicast().onBackpressureBuffer();

    // Store sinks for each client (thread-safe)
    private final Map<String, Many<String>> clientSinks = new ConcurrentHashMap<>();

    public ChatWebSocketHandler(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        String sessionId = session.getId();

        // store session sink
        Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();
        clientSinks.put(sessionId, sink);
        System.out.println("*** New session: " + sessionId);

        // Stream of incoming messages from this session
        Mono<Void> incomingFlux = session.receive()
            .publishOn(Schedulers.boundedElastic())
            .map(msg -> {
                String content = msg.getPayloadAsText();
                System.out.println("**** content: " + content);

                return messageService.saveMessage(sessionId, content, null)
                    .flatMap(messageEntity -> {
                        System.out.println("**** messageEntity 1 : " + messageEntity);
                        MessageDTO messageDTO = new MessageDTO(messageEntity.getId(), messageEntity.getSender(), messageEntity.getContent(), messageEntity.getTimestamp().toString(), messageEntity.getParentId());
                        return Mono.just(messageDTO);
                    })
                    .map(messageDTO -> {
                        System.out.println("**** messageDTO 2nd: " + messageDTO);
                        try {
                            return objectMapper.writeValueAsString(messageDTO);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .doOnNext(this::sendMessageToClients)
                    .then()
                    .subscribe();
            })
//            .doOnNext(chatSink::tryEmitNext)
            .then();

        Mono<Void> outgoingMessages = session.send(sink.asFlux().map(session::textMessage));

        return Mono.zip(incomingFlux, outgoingMessages).then();
    }

    private void sendMessageToClients(String jsonString) {
        // this sends to all clients
        System.out.println("**** sendMessageToClients: " + jsonString);
        clientSinks.values().forEach(sink -> sink.tryEmitNext(jsonString));

        // if we need to filter, we can iterate through the clients and send to specific ones instead
    }
}
