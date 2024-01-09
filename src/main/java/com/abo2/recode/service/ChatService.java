package com.abo2.recode.service;

import com.abo2.recode.domain.chatroom.ChatRoom;
import com.abo2.recode.domain.chatroom.ChatRoomRepository;
import com.abo2.recode.domain.chatroom.ChatRoomUserLink;
import com.abo2.recode.domain.chatroom.ChatRoomUserLinkRepository;
import com.abo2.recode.domain.user.User;
import com.abo2.recode.domain.user.UserRepository;
import com.abo2.recode.dto.chat.ChatReqDto;
import com.abo2.recode.handler.ex.CustomApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ChatRoomUserLinkRepository chatRoomUserLinkRepository;
    private final ChatRoomRepository chatRoomRepository;

    private final UserRepository userRepository;

    private final Logger logger = LoggerFactory.getLogger(ChatService.class);

    @Autowired
    public ChatService(ChatRoomUserLinkRepository chatRoomUserLinkRepository,
                       ChatRoomRepository chatRoomRepository,UserRepository userRepository) {
        this.chatRoomUserLinkRepository = chatRoomUserLinkRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.userRepository = userRepository;
    }

    public List<Long> getChatRoomNameListByUser(Long userId) {
       return chatRoomUserLinkRepository.getChatRoomsByUser(userId);
    }

    public List<String> getUserNameListBychatRoomId(Long chatRoomId) {
        List<Long> userIdList = chatRoomUserLinkRepository.getUserIdBychatRoomId(chatRoomId);

        List<String> usernameList = userRepository.findAllById(userIdList)
                .stream()
                .map(User::getNickname)
                .collect(Collectors.toList());

        return usernameList;
    }

    public String getCreatedBychatRoomId(Long chatRoomId) {
        Long createdBy = chatRoomUserLinkRepository.findCreatedBychatRoomId(chatRoomId);
            return userRepository.getNicknameByuserId(createdBy);
    }

    public ChatReqDto.ChatCreateReqDto createChatRoom(ChatReqDto.ChatCreateReqDto chatCreateReqDto) {
        // 1.chatroom Entity
        ChatRoom chatRoom = new ChatRoom(chatCreateReqDto.getChatRoomTitle());
        ChatRoom result = chatRoomRepository.save(chatRoom);
        User master = null;


        // 2. chatRoomUserLink Entity
        for(Long user_id : chatCreateReqDto.getUserIdList()){
            ChatRoomUserLink chatRoomUserLink = ChatRoomUserLink.builder()
                    .chatRoomId(result)
                    .userId(userRepository.findById(user_id).orElseThrow(
                            () -> new CustomApiException("채팅방 생성 중 오류 발생 : 참여 유저가 Null입니다.")
                    ))
                    .master(chatCreateReqDto.getMaster())
                    .build();
            master = chatRoomUserLinkRepository.save(chatRoomUserLink).getMaster();
        }

        return ChatReqDto.ChatCreateReqDto.builder()
                .chatRoomTitle(result.getTitle())
                .master(master)
                .userIdList(chatCreateReqDto.getUserIdList())
                .build();
    }

    public String getchatRoomTitleBychatRoomId(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId).orElseThrow(
                () -> new CustomApiException("특정 유저가 참여중인 채팅방 목록 출력 중 오류 발생 : 존재하지 않는 채팅방")
        ).getTitle();
    }

    @Transactional
    public ChatRoom deleteChatRoom(Long userId,Long chatRoomId) {

        // 채팅방 삭제하는 사람이 채팅방 만든 사람인지 체크
        if(
                userId  != chatRoomUserLinkRepository.getMasterBychatRoomId(chatRoomId).get(0)
        ) {
            throw new CustomApiException("채팅방 생성자만 채팅방을 삭제 할 수 있습니다.");
        }
       ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(
               () -> new CustomApiException("존재하지 않는 chatroom")
       );

        // chatroom,chatuserlink 삭제
        chatRoomUserLinkRepository.deleteBychatRoomId(chatRoomId);
        chatRoomRepository.deleteById(chatRoomId);

        // RestTemplate() -> chatting 삭제
        URI uri = UriComponentsBuilder
                .fromUriString("http://localhost:8080")
                .path("/chat/room/{chatRoomId}/delete")
                .buildAndExpand(chatRoomId)
                .encode()
                .toUri();

        RestTemplate restTemplate = new RestTemplate();

        try{
            restTemplate.delete(uri);
            logger.info("ChatRoom Delete complete");
        }
        catch (HttpClientErrorException e){
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                logger.error("Resource not found");
            } else {
                logger.error("DELETE request failed with status code: " + e.getStatusCode());
            }
        }catch (Exception e) {
            logger.error("An error occurred: " + e.getMessage());
        }

        return chatRoom;
    }

    public List<String> getNicknameList(Long chatRoomId) {

        List<Long> userIdList = chatRoomUserLinkRepository.getUserIdBychatRoomId(chatRoomId);
        List<String> nicknameList = new ArrayList<>();

        for(Long userId : userIdList){
            String nickname = userRepository.getNicknameByuserId(userId);
            nicknameList.add(nickname);
        }

        return nicknameList;
    }

    @Transactional
    public void leaveChatRoom(Long userId, Long chatRoomId) {

        // 나가는 사람이 채팅방 생성자인지 체크
        if(userId == chatRoomUserLinkRepository.getMasterBychatRoomId(chatRoomId).get(0)){

            // 같은 채팅방에 참여한 참여자 중 무작위로 채팅방 생성자 지위 넘김
            List<Long> userIdList = chatRoomUserLinkRepository.getUserIdBychatRoomId(chatRoomId);

            // 나와 아이디 일치하지 않는 아무 유저에게나 채팅방 주인 넘김
            for(Long OneofuserId : userIdList){
                if(OneofuserId != userId) {
                    chatRoomUserLinkRepository.updateCreatedBy(OneofuserId, chatRoomId);
                }
            }
        }

        // 나가는 사람이 채팅방의 마지막 사람인지 체크 -> 만약 맞으면 바로 채팅방 삭제
        if(chatRoomUserLinkRepository.getUserIdBychatRoomId(chatRoomId).size() == 1){
            deleteChatRoom(userId,chatRoomId);
            return;
        }

        // chatuserlink 삭제
        chatRoomUserLinkRepository.deleteBychatRoomIdAnduserId(userId,chatRoomId);
    }


}
