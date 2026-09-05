package com.example.bookshop.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatRoomResponse {
    private Long id;
    private Long userId;
    private String userName;
    private String status;
}