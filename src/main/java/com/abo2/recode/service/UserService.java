package com.abo2.recode.service;

import com.abo2.recode.domain.user.User;
import com.abo2.recode.domain.user.UserRepository;
import com.abo2.recode.dto.user.UserReqDto;
import com.abo2.recode.dto.user.UserRespDto;
import com.abo2.recode.handler.ex.CustomApiException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public UserRespDto.JoinRespDto 회원가입(UserReqDto.JoinReqDto joinReqDto) {
        // 1. 동일 유저네임 존재 검사
        Optional<User> userOP = userRepository.findByUsername(joinReqDto.getUsername());
        if (userOP.isPresent()) {
            throw new CustomApiException("동일한 username이 존재합니다.");
        }

        // 2. 패스워드 인코딩 + 회원가입
        User userPS = userRepository.save(joinReqDto.toEntity(passwordEncoder));

        // 3. dto 응답
        return new UserRespDto.JoinRespDto(userPS);
    }
    @Transactional
    public UserRespDto.FindUsernameRespDto findUsername(UserReqDto.FindUsernameReqDto findUsernameReqDto) {
        // 1. 이메일로 user 정보 조회
        User userPS = userRepository.findByEmail(findUsernameReqDto.getEmail()).orElseThrow(()
                -> new CustomApiException("존재하지 않는 사용자입니다."));

        // 2. dto 응답
        return new UserRespDto.FindUsernameRespDto(userPS.getUsername());

    }

    @Transactional
    public UserRespDto.UpdateUserRespDto updateUser(Long userId, UserReqDto.UpdateUserReqDto updateUserReqDto){
        // 1. user 아이디 조회
        User userPS = userRepository.findById(userId).orElseThrow(() -> new CustomApiException("존재하지 않는 사용자입니다."));

        // 2. update()로 객체에 변경사항 반영
        userPS.updateUser(updateUserReqDto.getNickname(), updateUserReqDto.getEmail());

        // 3. dto 응답
        return new UserRespDto.UpdateUserRespDto(userPS);
    }

    @Transactional
    public UserRespDto.WriteEssayResDto writeEssay(Long userId, UserReqDto.WriteEssayReqDto writeEssayReqDto){
        // 1. user 아이디 조회
        User userPS = userRepository.findById(userId).orElseThrow(() -> new CustomApiException("존재하지 않는 사용자입니다."));

        userPS.writeEssay(writeEssayReqDto.getEssay());

        return new UserRespDto.WriteEssayResDto(userPS);
    }
}