package com.Vishant.Convoe.controller;

import com.Vishant.Convoe.model.ChatGroup;
import com.Vishant.Convoe.repository.ChatGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupController {
    @Autowired
    private ChatGroupRepository groupRepo;

    @GetMapping
    public List<ChatGroup> getAllGroups() {
        return groupRepo.findAll();
    }

    @PostMapping("/create")
    public ResponseEntity<ChatGroup> create(@RequestBody ChatGroup group) {
        return ResponseEntity.ok(groupRepo.save(group));
    }
}
