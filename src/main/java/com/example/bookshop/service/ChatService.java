package com.example.bookshop.service;

import com.example.bookshop.dto.request.ChatMessageRequest;
import com.example.bookshop.dto.response.ChatMessageResponse;
import com.example.bookshop.dto.response.ChatRoomResponse;
import com.example.bookshop.entity.ChatMessage;
import com.example.bookshop.entity.ChatRoom;
import com.example.bookshop.entity.User;
import com.example.bookshop.enums.RoomStatus;
import com.example.bookshop.repository.ChatMessageRepository;
import com.example.bookshop.repository.ChatRoomRepository;
import com.example.bookshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChatRoom getOrCreateRoom(Long userId) {
        return chatRoomRepository.findByUserIdAndStatus(userId, RoomStatus.OPEN)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));

                    ChatRoom newRoom = ChatRoom.builder()
                            .user(user)
                            .status(RoomStatus.OPEN)
                            .build();

                    return chatRoomRepository.save(newRoom);
                });
    }

    @Transactional
    public ChatMessageResponse saveMessage(ChatMessageRequest request, Long senderId) {
        ChatRoom chatRoom = chatRoomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng chat"));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người gửi"));

        ChatMessage message = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .content(request.getContent())
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(message);

        return ChatMessageResponse.builder()
                .id(savedMessage.getId())
                .roomId(chatRoom.getId())
                .senderId(sender.getId())
                .senderName(sender.getFullName()) // Giả sử User có field fullName
                .content(savedMessage.getContent())
                .createdAt(savedMessage.getCreatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getChatHistory(Long roomId) {
        List<ChatMessage> messages = chatMessageRepository.findByChatRoomIdOrderByCreatedAtAsc(roomId);

        return messages.stream().map(msg -> ChatMessageResponse.builder()
                .id(msg.getId())
                .roomId(msg.getChatRoom().getId())
                .senderId(msg.getSender().getId())
                .senderName(msg.getSender().getFullName())
                .content(msg.getContent())
                .createdAt(msg.getCreatedAt())
                .build()
        ).toList();
    }

    @Transactional
    public void closeRoom(Long roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng chat"));
        room.setStatus(RoomStatus.CLOSED);
        chatRoomRepository.save(room);
    }
    public List<ChatRoomResponse> getRoomsByStatus(RoomStatus status) {
    List<ChatRoom> rooms = chatRoomRepository.findByStatusOrderByUpdatedAtDesc(status);
    return rooms.stream().map(room -> ChatRoomResponse.builder()
            .id(room.getId())
            .userId(room.getUser().getId())
            .userName(room.getUser().getFullName()) // Hoặc getEmail()
            .status(room.getStatus().name())
            .build()
    ).toList();
        }
}
