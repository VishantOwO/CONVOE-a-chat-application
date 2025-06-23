package com.Vishant.Convoe.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sender;
    private String content;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private ChatGroup group;

    private LocalDateTime timestamp = LocalDateTime.now();

    //Constructors and getter and setters
    public ChatMessage() {
    }

    public ChatMessage(String sender, String content) {
        this.sender = sender;
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setGroup(ChatGroup group) {
        this.group=group;
    }
}
