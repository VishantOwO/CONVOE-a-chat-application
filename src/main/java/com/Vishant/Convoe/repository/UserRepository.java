package com.Vishant.Convoe.repository;

import com.Vishant.Convoe.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.username = :usernameOrEmail OR u.email = :usernameOrEmail")
    Optional<User> findByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);


    List<User> findAllByEnabledTrue();

    @Query("SELECT u FROM User u WHERE u.username LIKE %:search% OR u.email LIKE %:search%")
    List<User> findByUsernameOrEmailContaining(@Param("search") String search);

    @Query(value = "SELECT * FROM users u WHERE u.id NOT IN (SELECT user_id FROM group_members WHERE group_id = :groupId)", nativeQuery = true)
    List<User> findUsersNotInGroup(@Param("groupId") Long groupId);


}
