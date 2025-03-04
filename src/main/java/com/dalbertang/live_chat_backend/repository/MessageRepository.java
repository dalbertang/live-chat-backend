package com.dalbertang.live_chat_backend.repository;

import com.dalbertang.live_chat_backend.model.MessageDTO;
import com.dalbertang.live_chat_backend.model.MessageEntity;
import java.util.UUID;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface MessageRepository extends ReactiveCrudRepository<MessageEntity, String> {

    // Get all messages that are NOT replies
//    @Query("SELECT * FROM messages WHERE parent_id IS NULL ORDER BY timestamp ASC")
//    Flux<MessageDTO> findTopLevelMessages();

//    // Get replies for a specific message
//    @Query("SELECT * FROM messages WHERE parent_id = :parentId ORDER BY timestamp ASC")
//    Flux<MessageEntity> findReplies(String parentId);

//    Flux<MessageEntity> findByParentId(UUID parentId); // Fetch threaded replies
}
