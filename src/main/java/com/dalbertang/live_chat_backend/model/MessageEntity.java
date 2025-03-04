package com.dalbertang.live_chat_backend.model;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("messages")
public class MessageEntity {
    @Id
    private String id;             // Message ID
    private String sender;       // Sender's username or ID
    private String content;      // Message text
    private Instant timestamp;   // Time of message
    private String parentId;       // Parent message ID (for threads)

}
