package com.Vishant.Convoe.repository;

import com.Vishant.Convoe.model.GroupMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupMessageRepository extends JpaRepository<GroupMessage, Long> {

    @Query("SELECT gm FROM GroupMessage gm WHERE gm.group.id = :groupId ORDER BY gm.sentAt ASC")
    List<GroupMessage> findByGroupIdOrderBySentAtAsc(@Param("groupId") Long groupId);

    @Query("SELECT gm FROM GroupMessage gm WHERE gm.group.id = :groupId ORDER BY gm.sentAt DESC")
    List<GroupMessage> findByGroupIdOrderBySentAtDesc(@Param("groupId") Long groupId);

    @Query("SELECT gm FROM GroupMessage gm WHERE gm.group.id = :groupId " +
            "AND gm.content LIKE %:search% ORDER BY gm.sentAt DESC")
    List<GroupMessage> searchMessagesInGroup(@Param("groupId") Long groupId,
                                             @Param("search") String search);

    @Query("SELECT COUNT(gm) FROM GroupMessage gm WHERE gm.group.id = :groupId")
    Long countMessagesByGroupId(@Param("groupId") Long groupId);
}