package com.dalbertang.live_chat_backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private String id;
    private String sender;
    private String content;
    private String timestamp;
    private String parentId; // If null, it's a top-level message; otherwise, it's a reply

//    public ChatMessage(String message) {
//        this.sender = parts[0];
//        this.content = parts[1];
//        this.timestamp = parts[2];
//        this.parentId = parts.length > 3 ? parts[3] : null;
//    }
}
