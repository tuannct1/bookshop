package com.example.bookshop.service;

import com.example.bookshop.dto.request.PublisherRequestDTO;
import com.example.bookshop.dto.response.PublisherResponseDTO;
import com.example.bookshop.entity.Publisher;
import com.example.bookshop.mapper.PublisherMapper;
import com.example.bookshop.repository.PublisherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublisherService {

    private final PublisherRepository publisherRepository;
    private final PublisherMapper publisherMapper;

    @Transactional
    public PublisherResponseDTO createPublisher(PublisherRequestDTO requestDTO) {
        Publisher publisher = publisherMapper.toEntity(requestDTO);
        Publisher savedPublisher = publisherRepository.save(publisher);
        return publisherMapper.toResponse(savedPublisher);
    }

    public PublisherResponseDTO getPublisherById(Long id) {
        Publisher publisher = publisherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà xuất bản với id: " + id));
        return publisherMapper.toResponse(publisher);
    }

    public List<PublisherResponseDTO> getAllPublishers() {
        return publisherRepository.findAll().stream()
                .map(publisherMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public PublisherResponseDTO updatePublisher(Long id, PublisherRequestDTO requestDTO) {
        Publisher publisher = publisherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà xuất bản với id: " + id));
        
        publisherMapper.updateEntity(publisher, requestDTO);
        
        Publisher updatedPublisher = publisherRepository.save(publisher);
        return publisherMapper.toResponse(updatedPublisher);
    }

    @Transactional
    public void deletePublisher(Long id) {
        if (!publisherRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy nhà xuất bản với id: " + id);
        }
        publisherRepository.deleteById(id);
    }
}