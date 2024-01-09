package com.abo2.recode.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findById(Long userId);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<User> findByEmailCheckToken(String emailCheckToken);

    void deleteById(Long userId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Post p SET p.user_id = null WHERE p.user_id = :userId", nativeQuery = true)
    void dissociatePosts(@Param("userId") Long userId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM Post_Reply WHERE user_id = :userId", nativeQuery = true)
    void dissociatePostReply(@Param("userId") Long userId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Qna q SET q.user_id = null WHERE q.user_id = :userId", nativeQuery = true)
    void dissociateQnas(@Param("userId") Long userId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Study_Member sm SET sm.user_id = null WHERE sm.user_id = :userId", nativeQuery = true)
    void dissociateStudyMember(@Param("userId") Long userId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Quiz q SET q.user_id = null WHERE q.user_id = :userId", nativeQuery = true)
    void dissociateQuiz(@Param("userId") Long userId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM Attendance WHERE user_id = :userId", nativeQuery = true)
    void deleteUsersAttendance(@Param("userId") Long userId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM Users WHERE user_id = :userId", nativeQuery = true)
    void deleteWithoutRelatedInfo(@Param("userId") Long userId);


    @Query(value = "SELECT nickname FROM Users u WHERE u.user_id = :userId", nativeQuery = true)
    String getNicknameByuserId(@Param("userId") Long userId);

    @Query(value = "SELECT user_id,nickname FROM Users u", nativeQuery = true)
    List<Map<Integer,String>> getUserList();

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM Notifications WHERE user_id = :userId", nativeQuery = true)
    void deleteUsersNotifications(@Param("userId") Long userId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM User_Badge WHERE user_id = :userId", nativeQuery = true)
    void deleteUsersUserBadge(@Param("userId") Long userId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Chat_Room_User_Link crul SET crul.user_id = null WHERE crul.user_id =:userId", nativeQuery = true)
    void dissociateChatRoomUserLink(@Param("userId") Long userId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM Qna_Reply FROM user_id =:userId", nativeQuery = true)
    void deleteUsersQnaReply(@Param("userId") Long userId);

}
