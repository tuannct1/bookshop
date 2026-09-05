package com.example.bookshop.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ChatMessageRequest {

    @NotNull(message = "Room ID không được để trống")
    private Long roomId;

    @NotBlank(message = "Nội dung tin nhắn không được để trống")
    private String content;

}