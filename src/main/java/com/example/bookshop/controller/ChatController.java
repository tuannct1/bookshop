package com.example.bookshop.controller;

import com.example.bookshop.dto.request.ChatMessageRequest;
import com.example.bookshop.dto.response.ChatMessageResponse;
import com.example.bookshop.service.ChatService;
import lombok.RequiredArgsConstructor;

import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    @MessageMapping("/chat.sendMessage")
    public void processMessage(@Payload ChatMessageRequest request, 
                               @Header("senderId") Long senderId) {
        
        ChatMessageResponse response = chatService.saveMessage(request, senderId);

        messagingTemplate.convertAndSend(
                "/topic/room/" + response.getRoomId(),
                response
        );

        messagingTemplate.convertAndSend("/topic/rooms", "REFRESH_ROOMS");
    }
}