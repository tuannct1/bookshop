package com.example.bookshop.controller;

import com.example.bookshop.dto.response.ChatMessageResponse;
import com.example.bookshop.dto.response.ChatRoomResponse;
import com.example.bookshop.entity.ChatRoom;
import com.example.bookshop.enums.RoomStatus;
import com.example.bookshop.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRestController {

    private final ChatService chatService;

    @PostMapping("/room")
    public ResponseEntity<Long> getOrCreateRoom(@RequestParam Long userId) {
        ChatRoom room = chatService.getOrCreateRoom(userId);
        return ResponseEntity.ok(room.getId());
    }

    @GetMapping("/history/{roomId}")
    public ResponseEntity<List<ChatMessageResponse>> getHistory(@PathVariable Long roomId) {
        return ResponseEntity.ok(chatService.getChatHistory(roomId));
    }

    @PutMapping("/room/{roomId}/close")
    public ResponseEntity<Void> closeRoom(@PathVariable Long roomId) {
        chatService.closeRoom(roomId);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomResponse>> getRooms(@RequestParam(defaultValue = "OPEN") RoomStatus status) {
    return ResponseEntity.ok(chatService.getRoomsByStatus(status));
}
}
