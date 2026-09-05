package com.example.bookshop.repository;

import com.example.bookshop.entity.ChatRoom;
import com.example.bookshop.enums.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    // Tìm phòng chat đang OPEN của khách hàng
    Optional<ChatRoom> findByUserIdAndStatus(Long userId, RoomStatus status);

    // Dành cho Admin: Lấy danh sách tất cả các phòng chat theo trạng thái
    List<ChatRoom> findByStatusOrderByUpdatedAtDesc(RoomStatus status);
}