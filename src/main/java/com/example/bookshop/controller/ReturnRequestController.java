package com.example.bookshop.controller;

import com.example.bookshop.dto.request.ReturnRequestDTO;
import com.example.bookshop.dto.response.ReturnResponseDTO;
import com.example.bookshop.enums.ReturnStatus;
import com.example.bookshop.security.CustomUserDetails;
import com.example.bookshop.service.ReturnRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/returns")
@RequiredArgsConstructor
public class ReturnRequestController {

    private final ReturnRequestService returnRequestService;

    @PostMapping("/order/{orderId}")
    public ResponseEntity<ReturnResponseDTO> createReturnRequest(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long orderId,
            @Valid @RequestBody ReturnRequestDTO requestDTO) { 
        
        Long userId = userDetails.getId();
        
        ReturnResponseDTO response = returnRequestService.createRequest(userId, orderId, requestDTO);
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @PutMapping("/{requestId}/status")
    public ResponseEntity<ReturnResponseDTO> updateReturnStatus(
            @PathVariable Long requestId,
            @RequestParam ReturnStatus status) {
        
        ReturnResponseDTO response = returnRequestService.updateReturnStatus(requestId, status);
        
        return ResponseEntity.ok(response);
    }
    @GetMapping
    public ResponseEntity<Page<ReturnResponseDTO>> getAllRequests(
            @RequestParam(required = false) ReturnStatus status,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        
        Page<ReturnResponseDTO> response = returnRequestService.getAllRequestsForAdmin(status, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-requests")
    public ResponseEntity<Page<ReturnResponseDTO>> getMyRequests(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        
        Long userId = userDetails.getId();
        Page<ReturnResponseDTO> response = returnRequestService.getMyReturnRequests(userId, pageable);
        return ResponseEntity.ok(response);
    }
}