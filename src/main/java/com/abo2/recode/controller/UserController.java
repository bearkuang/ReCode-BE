package com.abo2.recode.controller;

import com.abo2.recode.config.auth.LoginUser;
import com.abo2.recode.domain.user.User;
import com.abo2.recode.dto.ResponseDto;
import com.abo2.recode.dto.user.UserReqDto;
import com.abo2.recode.dto.user.UserRespDto;
import com.abo2.recode.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody @Valid UserReqDto.JoinReqDto joinReqDto, BindingResult bindingResult) {
        UserRespDto.JoinRespDto joinRespDto = userService.회원가입(joinReqDto);
        return new ResponseEntity<>(new ResponseDto<>(1, "회원가입 성공", joinRespDto), HttpStatus.CREATED);
    }

    @PostMapping("/find-username")
    public ResponseEntity<?> findUsername(@RequestBody @Valid UserReqDto.FindUsernameReqDto findUsernameReqDto, BindingResult bindingResult){
        UserRespDto.FindUsernameRespDto findUsernameRespDto = userService.findUsername(findUsernameReqDto);
        return  new ResponseEntity<>(new ResponseDto<>(1, "아이디 찾기 성공", findUsernameRespDto), HttpStatus.OK);
    }

    @PutMapping("/v1/users/{id}")
    public ResponseEntity<?> modifyUserInfo(@AuthenticationPrincipal LoginUser loginUser, @RequestBody @Valid UserReqDto.UpdateUserReqDto updateUserReqDto, BindingResult bindingResult){
        UserRespDto.UpdateUserRespDto updateUserRespDto = userService.updateUser(loginUser.getUser().getId(), updateUserReqDto);
        return new ResponseEntity<>(new ResponseDto<>(1,"회원정보 수정 성공", updateUserRespDto), HttpStatus.OK);
    }

    @PostMapping(value = "/v1/mypage/{id}/essay")
    public ResponseEntity<?> writeEssay(@AuthenticationPrincipal LoginUser loginUser, @RequestBody @Valid UserReqDto.WriteEssayReqDto writeEssayReqDto, BindingResult bindingResult){
        UserRespDto.WriteEssayResDto writeEssayResDto = userService.writeEssay(loginUser.getUser().getId(), writeEssayReqDto);
        return new ResponseEntity<>(new ResponseDto<>(1, "소개글이 성공적으로 업데이트 되었습니다.", writeEssayResDto), HttpStatus.OK);
    }
}