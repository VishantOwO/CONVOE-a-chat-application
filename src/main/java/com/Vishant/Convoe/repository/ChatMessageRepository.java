package com.Vishant.Convoe.repository;

import com.Vishant.Convoe.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByGroupId(Long groupId);
}