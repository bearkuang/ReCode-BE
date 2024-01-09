package com.abo2.recode.controller;

import com.abo2.recode.config.auth.LoginUser;
import com.abo2.recode.domain.chatroom.Chat;
import com.abo2.recode.domain.chatroom.ChatRoom;
import com.abo2.recode.dto.ResponseDto;
import com.abo2.recode.dto.chat.ChatReqDto;
import com.abo2.recode.dto.chat.ChatResDto;
import com.abo2.recode.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/api")
public class ChatController {

    private final ChatService chatService;

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    // 특정 유저가 참여중인 채팅방 목록 출력 -> 채팅 목록
    @GetMapping(value = "/v1/chat/chat-list")
    public ResponseEntity<?> getChatRoomsByUser(
            @AuthenticationPrincipal LoginUser loginUser
    ) {

        List<ChatResDto.ChatListDto> chatListDtoList = new ArrayList<>();

        // 1. 유저가 참여 중인 채팅방 목록 가져 오기
        List<Long> chatRoomIdList = chatService.getChatRoomNameListByUser(loginUser.getUser().getId());

        //2. 모든 chatRoomId에 대해 각 채팅방에 참여 중인 유저 목록 출력
        // 3. 각 chatRoomId에 대해 마지막 메세지 가져오기
        for (Long chatRoomId : chatRoomIdList) {
            List<String> usernameList = chatService.getUserNameListBychatRoomId(chatRoomId);

            logger.info("usernameList : " + usernameList);

            URI uri = UriComponentsBuilder
                    .fromUriString("http://localhost:8080")
                    .path("/chat/roomNum/{chatRoomId}/last-message")
                    .buildAndExpand(chatRoomId)
                    .encode()
                    .toUri();

            //getForObject
            RestTemplate restTemplate = new RestTemplate();
            String lastMessage;

            try {
                ResponseEntity<Chat> response = restTemplate.getForEntity(uri, Chat.class);
                if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                    lastMessage = response.getBody().getMsg();
                } else {
                    lastMessage = "There is no Message";
                }
            } catch (Exception e) {
                lastMessage = "There is no Message";
            }

            logger.info("lastMessage : " + lastMessage);

            //4. ChatListDto , chatListDtoList에 담기
            ChatResDto.ChatListDto chatListDto = ChatResDto.ChatListDto.builder()
                    .chatRoomId(chatRoomId)
                    .usernameList(usernameList)
                    .lastMessage(lastMessage)
                    .title(chatService.getchatRoomTitleBychatRoomId(chatRoomId))
                    .createdBy(chatService.getCreatedBychatRoomId(chatRoomId))
                    .build();

            chatListDtoList.add(chatListDto);
        }
        return new ResponseEntity<>(new ResponseDto<>(1, "현재 참여 중인 채팅방 목록입니다.", chatListDtoList), HttpStatus.OK);
    }

    // 참여 중인 채팅방 나가기
    @DeleteMapping(value = "/v1/chat/{chatRoomId}/leave-chatRoom")
    public ResponseEntity<?> leaveChatRoom(
            @AuthenticationPrincipal LoginUser loginUser,
            @PathVariable(name = "chatRoomId") Long chatRoomId
    ) {
        chatService.leaveChatRoom(loginUser.getUser().getId(), chatRoomId);
        return new ResponseEntity<>(new ResponseDto<>(1, "채팅방에서 나갔습니다.", chatRoomId), HttpStatus.OK);
    }

    // 채팅방 생성
    @PostMapping(value = "/v1/chat/chatRoom")
    public ResponseEntity<?> createChatRoom(
            @AuthenticationPrincipal LoginUser loginUser,
            @RequestBody ChatReqDto.ChatCreateReqDto chatCreateReqDto
    ) {
        chatCreateReqDto.setMaster(loginUser.getUser());
        // 1.chatroom Entity,chatRoomUserLink Entity
        ChatReqDto.ChatCreateReqDto chatcreatedtoresult = chatService.createChatRoom(chatCreateReqDto);
        return new ResponseEntity<>(new ResponseDto<>(1, "채팅방을 생성했습니다.", chatcreatedtoresult), HttpStatus.OK);
    }

    // 채팅방 삭제
    @DeleteMapping(value = "/v1/chat/{chatRoomId}/delete-chatRoom")
    public ResponseEntity<?> deleteChatRoom(
            @AuthenticationPrincipal LoginUser loginUser,
            @PathVariable(name = "chatRoomId") Long chatRoomId
    ) {
        // chatroom,chatuserlink 삭제
        // RestTemplate() -> chatting 삭제
        ChatRoom chatRoom = chatService.deleteChatRoom(loginUser.getUser().getId(), chatRoomId);

        ChatResDto.ChatDeleteResDto chatDeleteResDto = ChatResDto.ChatDeleteResDto.builder()
                .id(chatRoom.getId())
                .title(chatRoom.getTitle())
                .build();

        return new ResponseEntity<>(new ResponseDto<>(1, "채팅방을 삭제했습니다.", chatDeleteResDto), HttpStatus.OK);
    }

    // 특정 채팅방에 참여 중인 유저 목록 출력 -> 채팅 창
    // 특정 채팅방 입장 -> username,chatRoomId
    @GetMapping(value = "/v1/chat/{chatRoomId}/user-list")
    public ResponseEntity<?> getUserList(
            @PathVariable(name = "chatRoomId") Long chatRoomId
    ) {
        List<String> nicknameList = chatService.getNicknameList(chatRoomId);
        return new ResponseEntity<>(new ResponseDto<>(1, "현재 채팅방에 참여중인 유저 리스트입니다.", nicknameList), HttpStatus.OK);
    }

}