package com.Vishant.Convoe.controller;

import com.Vishant.Convoe.model.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatController {

    // Controller Method to Handle Messages
    @MessageMapping("/sendMessage/{groupId}")
    @SendTo("/topic/group.{groupId}")
    public ChatMessage sendMessage(@DestinationVariable String groupId, ChatMessage message) {
        System.out.println("Received message for group " + groupId + ": " + message.getContent());
        return message;
    }

    @GetMapping("chat")
    public String chat(){
        return "chat";
    }

}
