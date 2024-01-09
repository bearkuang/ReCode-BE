package com.abo2.recode.service;

import com.abo2.recode.domain.attendance.AttendanceRepository;
import com.abo2.recode.domain.badge.Badge;
import com.abo2.recode.domain.badge.BadgeRepository;
import com.abo2.recode.domain.badge.UserBadge;
import com.abo2.recode.domain.badge.UserBadgeRepository;
import com.abo2.recode.domain.chatroom.ChatRoomUserLinkRepository;
import com.abo2.recode.domain.estimate.StudyMemberEstimateRepository;
import com.abo2.recode.domain.notification.NotificationRepository;
import com.abo2.recode.domain.post.PostReplyRepository;
import com.abo2.recode.domain.post.PostRepository;
import com.abo2.recode.domain.qna.QnaReplyRepository;
import com.abo2.recode.domain.qna.QnaRepository;
import com.abo2.recode.domain.quiz.QuizRepository;
import com.abo2.recode.domain.skill.StudySkill;
import com.abo2.recode.domain.skill.StudySkillRepository;
import com.abo2.recode.domain.studymember.StudyMember;
import com.abo2.recode.domain.studymember.StudyMemberRepository;
import com.abo2.recode.domain.studyroom.StudyRoomRepository;
import com.abo2.recode.domain.user.User;
import com.abo2.recode.domain.user.UserRepository;
import com.abo2.recode.dto.study.StudyResDto;
import com.abo2.recode.dto.user.UserReqDto;
import com.abo2.recode.dto.user.UserRespDto;
import com.abo2.recode.handler.ex.CustomApiException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final StudyMemberRepository studyMemberRepository;
    private final StudySkillRepository studySkillRepository;

    private final StudyRoomRepository studyRoomRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final BadgeRepository badgeRepository;
    private final PostRepository postRepository;
    private final QnaRepository qnaRepository;
    private final QuizRepository quizRepository;
    private final AttendanceRepository attendanceRepository;
    private final NotificationRepository notificationRepository;
    private final PostReplyRepository postReplyRepository;
    private final ChatRoomUserLinkRepository chatRoomUserLinkRepository;
    private final QnaReplyRepository qnaReplyRepository;
    private final StudyMemberEstimateRepository studyMemberEstimateRepository;

    @Transactional
    public UserRespDto.JoinRespDto 회원가입(UserReqDto.JoinReqDto joinReqDto) {
        // 1. 동일 유저네임 존재 검사
        Optional<User> userOP = userRepository.findByUsername(joinReqDto.getUsername());
        if (userOP.isPresent()) {
            throw new CustomApiException("동일한 username이 존재합니다.");
        }

        // 2. 동일한 이메일 존재 검사
        Optional<User> userEM = userRepository.findByEmail(joinReqDto.getEmail());
        if (userEM.isPresent()) {
            throw new CustomApiException("동일한 email 로 가입된 계정이 존재합니다.");
        }

        // 3. 패스워드 인코딩 + 회원가입
        User userPS = userRepository.save(joinReqDto.toEntity(passwordEncoder));

        // 4. 뱃지 생성 및 초기화
        UserBadge userBadge = new UserBadge();
        userBadge.setUser(userPS);
        userBadge.setPoint(0);

        Badge defaultBadge = badgeRepository.findById(1L).orElseThrow(() -> new CustomApiException("기본 뱃지를 찾을 수 없습니다."));
        userBadge.setBadge(defaultBadge);

        userBadgeRepository.save(userBadge);

        // 4. dto 응답
        return new UserRespDto.JoinRespDto(userPS);
    }

    @Transactional
    public UserRespDto.JoinRespDto admin_join(UserReqDto.JoinAdminReqDto joinAdminReqDto) {
        // 1. 동일 유저네임 존재 검사
        Optional<User> userOP = userRepository.findByUsername(joinAdminReqDto.getUsername());
        if (userOP.isPresent()) {
            throw new CustomApiException("동일한 username이 존재합니다.");
        }

        // 2. 패스워드 인코딩 + 회원가입
        User userPS = userRepository.save(joinAdminReqDto.toEntity(passwordEncoder));

        // 3. dto 응답
        return new UserRespDto.JoinRespDto(userPS);
    }

    @Transactional
    public boolean checkUsernameDuplicate(String username) {
        // 1. 회원가입 시 username 중복확인
        return userRepository.existsByUsername(username);
    }

    @Transactional
    public boolean checkEmailDuplicate(String email) {
        // 1. 회원가입 시 email 중복확인
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public boolean checkNicknameDuplicate(String nickname){
        // 1. 회원가입 시 email 중복확인
        return userRepository.existsByNickname(nickname);
    }

    @Transactional
    public UserRespDto.FindUsernameRespDto findUsername(UserReqDto.FindUsernameReqDto findUsernameReqDto) {
        // 1. 이메일로 user 정보 조회
        User userPS = userRepository.findByEmail(findUsernameReqDto.getEmail()).orElseThrow(() -> new CustomApiException("존재하지 않는 사용자입니다."));

        // 2. dto 응답
        return new UserRespDto.FindUsernameRespDto(userPS.getUsername());

    }

    @Transactional
    public UserRespDto.UpdateUserRespDto updateUser(Long userId, UserReqDto.UpdateUserReqDto updateUserReqDto) {
        // 1. user 아이디 조회
        User userPS = userRepository.findById(userId).orElseThrow(() -> new CustomApiException("존재하지 않는 사용자입니다."));

        // 2. update()로 객체에 변경사항 반영
        userPS.updateUser(updateUserReqDto.getNickname(), updateUserReqDto.getEmail());
        // 3. dto 응답
        return new UserRespDto.UpdateUserRespDto(userPS);
    }

    @Transactional
    public UserRespDto.EssayRespDto writeEssay(Long userId, UserReqDto.WriteEssayReqDto writeEssayReqDto) {
        // 1. user 아이디 조회
        User userPS = userRepository.findById(userId).orElseThrow(() -> new CustomApiException("존재하지 않는 사용자입니다."));

        // 2. 객체에 변경 사항 반영
        userPS.writeEssay(writeEssayReqDto.getEssay());

        // 3. dto 응답
        return new UserRespDto.EssayRespDto(userPS);
    }

    @Transactional
    public UserRespDto.EssayRespDto getEssay(Long userId) {
        // 1. user 아이디 조회
        User userPS = userRepository.findById(userId).orElseThrow(() -> new CustomApiException("존재하지 않는 사용자입니다."));

        // 2. dto 응답
        return new UserRespDto.EssayRespDto(userPS);
    }

    @Transactional
    public void withdrawUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomApiException("존재하지 않는 사용자입니다."));

        // 가입 중인 스터디룸 중에서 스터디장의 권한으로 있는 스터디 룸이 있는지 확인
        boolean isMasterOfAnyRoom = studyRoomRepository.existsByMaster(user);

        if (isMasterOfAnyRoom) {
            throw new CustomApiException("스터디장의 권한을 갖고 계신 스터디룸이 존재합니다. 권한을 양도한 후에 탈퇴를 진행해주시기 바랍니다.");
        }

        if (studyMemberRepository.existsById(userId)) {
            // 스터디장의 권한으로 있는 스터디 룸이 없다면 스터디룸에 작성한 글, 퀴즈, Qna 에서 작성한 글을 제외한 정보 삭제
            studyMemberRepository.dissociateByUserId(userId);
            if (postRepository.existsByUserId(userId)) {
                userRepository.dissociatePosts(userId);
            }
            if(postReplyRepository.existsByUserId(userId)){
                userRepository.dissociatePostReply(userId);
            }
            if (qnaRepository.existsByUserId(userId)) {
                userRepository.dissociateQnas(userId);
            }
            if (qnaReplyRepository.existsByUserId(userId)){
                userRepository.deleteUsersQnaReply(userId);
            }
            if (quizRepository.existsByUserId(userId)) {
                userRepository.dissociateQuiz(userId);
            }
            if (attendanceRepository.existsByUserId(userId)) {
                userRepository.deleteUsersAttendance(userId);
            }
            if (chatRoomUserLinkRepository.existsByUserId(user)) {
                userRepository.dissociateChatRoomUserLink(userId);
            }
            if (notificationRepository.existsByUserId(userId)) {
                userRepository.deleteUsersNotifications(userId);
            }
            userRepository.deleteUsersUserBadge(userId);
            userRepository.deleteWithoutRelatedInfo(userId);
        } else {
            userRepository.deleteUsersUserBadge(userId);
            userRepository.deleteById(userId);
        }
    }


    @Transactional
    public UserRespDto.GetUserInfoDto getUserInfo(Long userId) {
        // 1. user 아이디 조회
        User userPS = userRepository.findById(userId).orElseThrow(() -> new CustomApiException("존재하지 않는 사용자입니다."));

        // 2. dto 응답
        return new UserRespDto.GetUserInfoDto(userPS);
    }

    public List<StudyResDto.MyStudyRespDto> myStudy(Long userId) {
        List<StudyMember> studyMembers = studyMemberRepository.findByUserId(userId);
        List<StudyResDto.MyStudyRespDto> myStudyRespDtos = new ArrayList<>();

        for (StudyMember studyMember : studyMembers) {
            List<StudySkill> skills = studySkillRepository.findByStudyRoomId(studyMember.getStudyRoom().getId());
            StudyResDto.MyStudyRespDto myStudyRespDto = new StudyResDto.MyStudyRespDto(studyMember, skills);
            myStudyRespDtos.add(myStudyRespDto);
        }

        return myStudyRespDtos;
    }

    public User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("해당 ID의 사용자를 찾을 수 없습니다."));
    }

    @Transactional
    public UserRespDto.ChangePasswordRespDto changePassword(Long userId, UserReqDto.ChangePasswordReqDto changePasswordReqDto) {
        // 1. 사용자 찾기
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomApiException("존재하지 않는 사용자입니다."));

        // 2. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(changePasswordReqDto.getPassword());

        // 3. 암호화된 비밀번호로 변경
        user.changePassword(encodedPassword);

        // 4. 변경된 사용자 정보 저장
        userRepository.save(user);

        return new UserRespDto.ChangePasswordRespDto(user);
    }

    @Transactional
    public UserRespDto.ChangePasswordRespDto changePasswordWithToken(String emailCheckToken, UserReqDto.ChangePasswordReqDto changePasswordReqDto) {
        // 1. 토큰 유효성 검증 및 사용자 찾기
        User user = userRepository.findByEmailCheckToken(emailCheckToken).orElseThrow(() -> new CustomApiException("유효하지 않은 토큰입니다."));

        // 2. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(changePasswordReqDto.getPassword());

        // 3. 암호화된 비밀번호로 변경
        user.changePassword(encodedPassword);

        // 4. 변경된 사용자 정보 저장
        userRepository.save(user);

        return new UserRespDto.ChangePasswordRespDto(user);
    }

    public List<Map<Integer, String>> getUserList() {
        return userRepository.getUserList();
    }
}
