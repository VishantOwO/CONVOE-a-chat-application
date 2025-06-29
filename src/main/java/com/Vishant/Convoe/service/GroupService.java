package com.Vishant.Convoe.service;



import com.Vishant.Convoe.model.ChatGroup;
import com.Vishant.Convoe.model.GroupMessage;
import com.Vishant.Convoe.model.User;
import com.Vishant.Convoe.repository.ChatGroupRepository;
import com.Vishant.Convoe.repository.GroupMessageRepository;
import com.Vishant.Convoe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GroupService {

    @Autowired
    private ChatGroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupMessageRepository messageRepository;

    public ChatGroup createGroup(String name, String description, Long createdByUserId) {
        User creator = userRepository.findById(createdByUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ChatGroup group = new ChatGroup(name, description, creator);
        return groupRepository.save(group);
    }

    public List<ChatGroup> getUserGroups(Long userId) {
        return groupRepository.findGroupsByUserId(userId);
    }

    public Optional<ChatGroup> getGroupById(Long groupId) {
        return groupRepository.findById(groupId);
    }

    public ChatGroup getGroupByIdAndUser(Long groupId, User user) {
        return groupRepository.findByIdAndMembers(groupId, user);
    }

    public List<ChatGroup> searchGroups(String name) {
        return groupRepository.findByNameContaining(name);
    }

    public ChatGroup addMemberToGroup(Long groupId, Long userId) {
        ChatGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("ChatGroup not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        group.addMember(user);
        return groupRepository.save(group);
    }

    public ChatGroup removeMemberFromGroup(Long groupId, Long userId) {
        ChatGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("ChatGroup not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        group.removeMember(user);
        return groupRepository.save(group);
    }

    public void sendMessage(Long groupId, Long userId, String content) {
        // Check if group exists
        ChatGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // Check if user is member of group
        if (!isUserMemberOfGroup(groupId, userId)) {
            throw new RuntimeException("User is not a member of this group");
        }

        // Get user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create and save message
        GroupMessage message = new GroupMessage();
        message.setContent(content);
        message.setSender(user);
        message.setGroup(group);
        message.setSentAt(LocalDateTime.now()); // or new Date()

        messageRepository.save(message);
    }

    public List<GroupMessage> getGroupMessages(Long groupId) {
        return messageRepository.findByGroupIdOrderBySentAtAsc(groupId);
    }

    public List<User> getAvailableUsersForGroup(Long groupId) {
        return userRepository.findUsersNotInGroup(groupId);
    }

    public Long getGroupMemberCount(Long groupId) {
        return groupRepository.countMembersByGroupId(groupId);
    }

    public Long getGroupMessageCount(Long groupId) {
        return messageRepository.countMessagesByGroupId(groupId);
    }

    public boolean isUserMemberOfGroup(Long groupId, Long userId) {
        ChatGroup group = groupRepository.findById(groupId).orElse(null);
        if (group == null) return false;

        return group.getMembers().stream()
                .anyMatch(member -> member.getId().equals(userId));
    }

    public void deleteGroup(Long groupId, Long userId) {
        ChatGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("ChatGroup not found"));

        // Only the creator can delete the group
        if (!group.getCreatedBy().getId().equals(userId)) {
            throw new RuntimeException("Only the group creator can delete the group");
        }

        groupRepository.delete(group);
    }

    public ChatGroup updateGroup(Long groupId, String name, String description, Long userId) {
        ChatGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("ChatGroup not found"));

        // Only the creator can update the group
        if (!group.getCreatedBy().getId().equals(userId)) {
            throw new RuntimeException("Only the group creator can update the group");
        }

        group.setName(name);
        group.setDescription(description);
        return groupRepository.save(group);
    }
}

