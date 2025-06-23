package com.Vishant.Convoe.controller;

import org.springframework.ui.Model;
import com.Vishant.Convoe.model.ChatGroup;
import com.Vishant.Convoe.model.ChatMessage;
import com.Vishant.Convoe.repository.ChatGroupRepository;
import com.Vishant.Convoe.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatController {

    @Autowired
    private ChatGroupRepository groupRepo;

    @Autowired
    private ChatMessageRepository messageRepo;

    @MessageMapping("/sendMessage/{groupId}")
    @SendTo("/topic/group.{groupId}")
    public ChatMessage handleMessage(@DestinationVariable Long groupId, ChatMessage message) {
        ChatGroup group = groupRepo.findById(groupId).orElseThrow();
        message.setGroup(group);
        messageRepo.save(message);
        return message;
    }

    @GetMapping("/chat")
    public String chatPage(Model model) {
        model.addAttribute("groups", groupRepo.findAll());
        return "chat"; // chat.html
    }

}
