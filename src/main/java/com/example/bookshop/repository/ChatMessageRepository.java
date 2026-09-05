package com.example.bookshop.repository;


import com.example.bookshop.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    // Lấy toàn bộ tin nhắn trong một phòng chat (sắp xếp tăng dần theo thời gian)
    List<ChatMessage> findByChatRoomIdOrderByCreatedAtAsc(Long roomId);
}