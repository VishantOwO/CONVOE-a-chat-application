package com.Vishant.Convoe.repository;


import com.Vishant.Convoe.model.ChatGroup;
import com.Vishant.Convoe.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatGroupRepository extends JpaRepository<ChatGroup, Long> {

    @Query("SELECT DISTINCT cg FROM ChatGroup cg LEFT JOIN cg.members m WHERE m.id = :userId OR cg.createdBy.id = :userId")
    List<ChatGroup> findGroupsByUserId(@Param("userId") Long userId);

    @Query("SELECT g FROM ChatGroup g WHERE g.name LIKE CONCAT('%', :name, '%')")
    List<ChatGroup> findByNameContaining(@Param("name") String name);


    @Query("SELECT COUNT(u) FROM ChatGroup g JOIN g.members u WHERE g.id = :groupId")
    Long countMembersByGroupId(@Param("groupId") Long groupId);

    @Query("SELECT g FROM ChatGroup g JOIN g.members m WHERE m = :user AND g.id = :groupId")
    ChatGroup findByIdAndMembers(@Param("groupId") Long groupId, @Param("user") User user);

}
