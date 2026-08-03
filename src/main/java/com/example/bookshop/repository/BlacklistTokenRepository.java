package com.example.bookshop.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.bookshop.entity.BlacklistToken;

public interface BlacklistTokenRepository extends CrudRepository<BlacklistToken, String>{

    
} 