package com.Vishant.Convoe.repository;

import com.Vishant.Convoe.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {}