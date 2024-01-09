package com.abo2.recode.domain.chatroom;

import com.abo2.recode.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomUserLinkRepository extends JpaRepository<ChatRoomUserLink, Long> {
    @Query(value = "SELECT chat_room_id as chatRoomId FROM chat_room_user_link WHERE user_id =:userId", nativeQuery = true)
    List<Long> getChatRoomsByUser(@Param(value = "userId") Long userId);

    @Query(value = "SELECT user_id as userId FROM chat_room_user_link WHERE chat_room_id =:chatRoomId", nativeQuery = true)
    List<Long> getUserIdBychatRoomId(@Param(value = "chatRoomId") Long chatRoomId);

    @Query(value = "SELECT created_by as master FROM chat_room_user_link WHERE chat_room_id =:chatRoomId", nativeQuery = true)
    List<Long> getMasterBychatRoomId(@Param(value = "chatRoomId") Long chatRoomId);

    @Query(value = "DELETE FROM chat_room_user_link WHERE chat_room_id =:chatRoomId", nativeQuery = true)
    void deleteBychatRoomId(@Param(value = "chatRoomId") Long chatRoomId);

    @Query(value = "DELETE FROM chat_room_user_link WHERE chat_room_id =:chatRoomId AND user_id=:userId", nativeQuery = true)
    void deleteBychatRoomIdAnduserId(@Param(value = "userId") Long userId, @Param(value = "chatRoomId") Long chatRoomId);

    @Query(value = "UPDATE chat_room_user_link SET created_by=:oneofuserId WHERE chat_room_id=:chatRoomId",nativeQuery = true)
    void updateCreatedBy(@Param(value = "oneofuserId") Long oneofuserId,@Param(value = "chatRoomId") Long chatRoomId);

    boolean existsByUserId(User user);

    @Query(value = "SELECT DISTINCT created_by FROM chat_room_user_link WHERE chat_room_id=:chatRoomId",nativeQuery = true)
    Long findCreatedBychatRoomId(@Param(value = "chatRoomId") Long chatRoomId);
}
