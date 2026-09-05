package com.example.bookshop.controller;

import com.example.bookshop.dto.request.AuthorRequestDTO;
import com.example.bookshop.dto.response.AuthorResponseDTO;
import com.example.bookshop.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @PostMapping
    public ResponseEntity<AuthorResponseDTO> createAuthor(@RequestBody AuthorRequestDTO requestDTO) {
        AuthorResponseDTO response = authorService.createAuthor(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED); 
        // Trả về mã 201 (Created) khi tạo thành công
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponseDTO> getAuthorById(@PathVariable Long id) {
        AuthorResponseDTO response = authorService.getAuthorById(id);
        return ResponseEntity.ok(response); 
    }

    @GetMapping
    public ResponseEntity<List<AuthorResponseDTO>> getAllAuthors() {
        List<AuthorResponseDTO> responses = authorService.getAllAuthors();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorResponseDTO> updateAuthor(
            @PathVariable Long id, 
            @RequestBody AuthorRequestDTO requestDTO) {
        AuthorResponseDTO response = authorService.updateAuthor(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.noContent().build(); 
    }
}