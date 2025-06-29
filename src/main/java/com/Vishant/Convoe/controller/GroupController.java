// GroupController.java
package com.Vishant.Convoe.controller;


import com.Vishant.Convoe.model.ChatGroup;
import com.Vishant.Convoe.model.GroupMessage;
import com.Vishant.Convoe.model.User;
import com.Vishant.Convoe.service.GroupService;
import com.Vishant.Convoe.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserService userService;

    // Show all groups for the current user
    @GetMapping
    public String showGroups(Model model, Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        if (userId == null) {
            return "redirect:/login";
        }

        List<ChatGroup> userGroups = groupService.getUserGroups(userId);
        model.addAttribute("groups", userGroups);
        model.addAttribute("currentUserId", userId);
        return "groups/list";
    }

    // Show createGroup group form
    @GetMapping("/createGroup")
    public String showCreateGroupForm(Model model, Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        if (userId == null) {
            return "redirect:/login";
        }

        model.addAttribute("group", new ChatGroup());
        return "createGroup";
    }

    // Handle group creation
    @PostMapping("/createGroup")
    public String createGroup(@RequestParam String name,
                              @RequestParam String description,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        Long userId = getCurrentUserId(authentication);
        if (userId == null) {
            return "redirect:/login";
        }

        try {
            ChatGroup group = groupService.createGroup(name, description, userId);
            redirectAttributes.addFlashAttribute("success", "ChatGroup created successfully!");
            return "redirect:/groups/" + group.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating group: " + e.getMessage());
            return "redirect:createGroup";
        }
    }

    // Show specific group chat
    @GetMapping("/{groupId}")
    public String showGroupChat(@PathVariable Long groupId,
                                Model model,
                                Authentication authentication) {

        System.out.println("=== DEBUG: Group Chat Request ===");
        System.out.println("Group ID: " + groupId);

        try {
            Long userId = getCurrentUserId(authentication);
            if (userId == null) {
                System.out.println("DEBUG: User ID is null, redirecting to login");
                return "redirect:/login";
            }
            System.out.println("DEBUG: User ID: " + userId);

            Optional<User> userOpt = userService.findById(userId);
            if (!userOpt.isPresent()) {
                System.out.println("DEBUG: User not found with ID: " + userId);
                return "redirect:/login";
            }
            System.out.println("DEBUG: User found: " + userOpt.get().getUsername());

            User currentUser = userOpt.get();

            // Debug: Check if group exists first
            System.out.println("DEBUG: Checking if group exists...");
            ChatGroup group = null;
            try {
                group = groupService.getGroupByIdAndUser(groupId, currentUser);
                if (group == null) {
                    System.out.println("DEBUG: Group is null - either doesn't exist or user has no access");
                    return "redirect:/groups?error=Group not found or access denied";
                }
                System.out.println("DEBUG: Group found: " + group.getName());
            } catch (Exception e) {
                System.out.println("DEBUG: Error getting group: " + e.getMessage());
                e.printStackTrace();
                return "redirect:/groups?error=Error loading group";
            }

            // Debug: Get messages
            System.out.println("DEBUG: Getting group messages...");
            List<GroupMessage> messages = null;
            try {
                messages = groupService.getGroupMessages(groupId);
                System.out.println("DEBUG: Found " + (messages != null ? messages.size() : 0) + " messages");
            } catch (Exception e) {
                System.out.println("DEBUG: Error getting messages: " + e.getMessage());
                e.printStackTrace();
                // Continue without messages for now
                messages = new ArrayList<>();
            }

            System.out.println("DEBUG: Adding attributes to model...");
            model.addAttribute("group", group);
            model.addAttribute("messages", messages);
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("newMessage", new GroupMessage());

            System.out.println("DEBUG: Returning template 'groups/chat'");
            return "groups/chat";

        } catch (Exception e) {
            System.out.println("DEBUG: Unexpected error in showGroupChat: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/groups?error=Unexpected error occurred";
        }
    }


    // Send message to group
    @PostMapping("/{groupId}/messages")
    public String sendMessage(@PathVariable Long groupId,
                              @RequestParam String content,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        Long userId = getCurrentUserId(authentication);
        if (userId == null) {
            return "redirect:/login";
        }

        try {
            groupService.sendMessage(groupId, userId, content);
            return "redirect:/groups/" + groupId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error sending message: " + e.getMessage());
            return "redirect:/groups/" + groupId;
        }
    }

    // Show group members
    @GetMapping("/{groupId}/members")
    public String showGroupMembers(@PathVariable Long groupId,
                                   Model model,
                                   Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        if (userId == null) {
            return "redirect:/login";
        }

        Optional<ChatGroup> groupOpt = groupService.getGroupById(groupId);
        if (!groupOpt.isPresent()) {
            return "redirect:/groups?error=ChatGroup not found";
        }

        ChatGroup group = groupOpt.get();
        if (!groupService.isUserMemberOfGroup(groupId, userId)) {
            return "redirect:/groups?error=Access denied";
        }

        List<User> availableUsers = groupService.getAvailableUsersForGroup(groupId);

        model.addAttribute("group", group);
        model.addAttribute("availableUsers", availableUsers);
        model.addAttribute("currentUserId", userId);

        return "groups/members";
    }

    // Add member to group
    @PostMapping("/{groupId}/members/add")
    public String addMember(@PathVariable Long groupId,
                            @RequestParam Long userId,
                            Authentication authentication,
                            RedirectAttributes redirectAttributes) {
        Long currentUserId = getCurrentUserId(authentication);
        if (currentUserId == null) {
            return "redirect:/login";
        }

        try {
            groupService.addMemberToGroup(groupId, userId);
            redirectAttributes.addFlashAttribute("success", "Member added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error adding member: " + e.getMessage());
        }

        return "redirect:/groups/" + groupId + "/members";
    }

    // Remove member from group
    @PostMapping("/{groupId}/members/remove")
    public String removeMember(@PathVariable Long groupId,
                               @RequestParam Long userId,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        Long currentUserId = getCurrentUserId(authentication);
        if (currentUserId == null) {
            return "redirect:/login";
        }

        try {
            groupService.removeMemberFromGroup(groupId, userId);
            redirectAttributes.addFlashAttribute("success", "Member removed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error removing member: " + e.getMessage());
        }

        return "redirect:/groups/" + groupId + "/members";
    }

    // Show edit group form
    @GetMapping("/{groupId}/edit")
    public String showEditGroupForm(@PathVariable Long groupId,
                                    Model model,
                                    Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        if (userId == null) {
            return "redirect:/login";
        }

        Optional<ChatGroup> groupOpt = groupService.getGroupById(groupId);
        if (!groupOpt.isPresent()) {
            return "redirect:/groups?error=ChatGroup not found";
        }

        ChatGroup group = groupOpt.get();
        if (!group.getCreatedBy().getId().equals(userId)) {
            return "redirect:/groups?error=Only group creator can edit";
        }

        model.addAttribute("group", group);
        return "groups/edit";
    }

    // Handle group update
    @PostMapping("/{groupId}/edit")
    public String updateGroup(@PathVariable Long groupId,
                              @RequestParam String name,
                              @RequestParam String description,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        Long userId = getCurrentUserId(authentication);
        if (userId == null) {
            return "redirect:/login";
        }

        try {
            groupService.updateGroup(groupId, name, description, userId);
            redirectAttributes.addFlashAttribute("success", "ChatGroup updated successfully!");
            return "redirect:/groups/" + groupId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating group: " + e.getMessage());
            return "redirect:/groups/" + groupId + "/edit";
        }
    }

    // Delete group
    @PostMapping("/{groupId}/delete")
    public String deleteGroup(@PathVariable Long groupId,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        Long userId = getCurrentUserId(authentication);
        if (userId == null) {
            return "redirect:/login";
        }

        try {
            groupService.deleteGroup(groupId, userId);
            redirectAttributes.addFlashAttribute("success", "ChatGroup deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting group: " + e.getMessage());
        }

        return "redirect:/groups";
    }

    // Leave group
    @PostMapping("/{groupId}/leave")
    public String leaveGroup(@PathVariable Long groupId,
                            Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        Long userId = getCurrentUserId(authentication);
        if (userId == null) {
            return "redirect:/login";
        }

        try {
            groupService.removeMemberFromGroup(groupId, userId);
            redirectAttributes.addFlashAttribute("success", "You have left the group!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error leaving group: " + e.getMessage());
        }

        return "redirect:/groups";
    }

    private Long getCurrentUserId(Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                System.out.println("DEBUG: Authentication is null or not authenticated");
                return null;
            }

            String username = authentication.getName();
            System.out.println("DEBUG: Authentication username: " + username);

            Optional<User> userOpt = userService.findByUsername(username);
            if (!userOpt.isPresent()) {
                System.out.println("DEBUG: User not found with username: " + username);
                return null;
            }

            Long userId = userOpt.get().getId();
            System.out.println("DEBUG: Found user ID: " + userId);
            return userId;

        } catch (Exception e) {
            System.out.println("DEBUG: Error in getCurrentUserId: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

}