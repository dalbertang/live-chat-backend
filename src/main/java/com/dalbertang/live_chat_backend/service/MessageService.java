package com.dalbertang.live_chat_backend.service;

import com.dalbertang.live_chat_backend.model.MessageEntity;
import com.dalbertang.live_chat_backend.repository.MessageRepository;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    // "sender" is the id of the user who sent the message
    public Mono<MessageEntity> saveMessage(String sender, String content, String parentId) {
        System.out.println("**** saveMessage: " + sender + " " + content + " " + parentId);
        MessageEntity message = new MessageEntity(null, sender, content, Instant.now(), parentId);
        return messageRepository.save(message)
            .doOnSuccess(savedMessage -> logger.info("*** Message saved: {}", savedMessage))
            .doOnError(error -> logger.error("*** Error saving message", error))
            .doOnTerminate(() -> System.out.println("**** saveMessage stream terminated"))
            .doOnSubscribe(subscription -> System.out.println("**** saveMessage stream subscribed"));
    }

//    public void testSaveMessage(MessageEntity message) {
//        StepVerifier.create(saveMessage(message))
//            .expectNextMatches(savedMessage -> savedMessage.getId() != null)
//            .verifyComplete();
//    }

//    public Flux<MessageEntity> getChatHistory() {
//        return messageRepository.findAll();
//    }
//
//    public Flux<MessageEntity> getThread(UUID parentId) {
//        return messageRepository.findByParentId(parentId);
//    }
}
