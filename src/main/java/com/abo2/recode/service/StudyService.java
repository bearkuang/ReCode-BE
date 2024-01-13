package com.abo2.recode.service;

import com.abo2.recode.domain.attendance.AttendanceDay;
import com.abo2.recode.domain.attendance.AttendanceDayRepository;
import com.abo2.recode.domain.badge.UserBadgeRepository;
import com.abo2.recode.domain.notification.NotificationRepository;
import com.abo2.recode.domain.skill.Skill;
import com.abo2.recode.domain.skill.SkillRepository;
import com.abo2.recode.domain.skill.StudySkill;
import com.abo2.recode.domain.skill.StudySkillRepository;
import com.abo2.recode.domain.studymember.StudyMember;
import com.abo2.recode.domain.studymember.StudyMemberRepository;
import com.abo2.recode.domain.studyroom.StudyRoom;
import com.abo2.recode.domain.studyroom.StudyRoomRepository;
import com.abo2.recode.domain.user.User;
import com.abo2.recode.domain.user.UserRepository;
import com.abo2.recode.dto.post.PostRespDto;
import com.abo2.recode.dto.study.StudyReqDto;
import com.abo2.recode.dto.study.StudyResDto;
import com.abo2.recode.handler.ex.CustomApiException;
import com.abo2.recode.handler.ex.CustomForbiddenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class StudyService {

    private final AttendanceDayRepository attendanceDayRepository;
    private final StudyRoomRepository studyRoomRepository;
    private final StudySkillRepository studySkillRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final StudyMemberRepository studyMemberRepository;
    private final NotificationRepository notificationRepository;

    private UserBadgeRepository userBadgeRepository;


    @Autowired
    public StudyService(AttendanceDayRepository attendanceDayRepository, AttendanceDayRepository attendanceDayRepository1, StudyRoomRepository studyRoomRepository,
                        StudySkillRepository studySkillRepository,
                        SkillRepository skillRepository,
                        UserRepository userRepository,
                        StudyMemberRepository studyMemberRepository,
                        UserBadgeRepository userBadgeRepository,
                        NotificationRepository notificationRepository) {
        this.attendanceDayRepository = attendanceDayRepository1;
        this.studyRoomRepository = studyRoomRepository;
        this.studySkillRepository = studySkillRepository;
        this.skillRepository = skillRepository;
        this.userRepository = userRepository;
        this.studyMemberRepository = studyMemberRepository;
        this.userBadgeRepository = userBadgeRepository;
        this.notificationRepository = notificationRepository;
    }

    //스터디 조장의 스터디 멤버 승인/거부
    @Transactional
    public void membershipUpdate(String status, Long studyId, Long userId) {

        if (status.equals("Approved")) {
            //StudyMember 테이블 업데이트(상태값 0 -> 1)
            studyMemberRepository.membershipUpdate(1, studyId, userId);

        } else if (status.equals("Rejected")) {
            //StudyMember 테이블 업데이트(상태값 0 -> 2)
            studyMemberRepository.membershipUpdate(2, studyId, userId);
        }
    }


    // 요일 문자열을 enum 타입으로 변환하는 메서드
    private DayOfWeek parseDayOfWeek(String dayOfWeekString) {
        try {
            return DayOfWeek.valueOf(dayOfWeekString);
        } catch (IllegalArgumentException e) {
            // 유효하지 않은 요일 값 처리
            throw new IllegalArgumentException("유효하지 않은 요일 값: " + dayOfWeekString);
        }
    }


    // 시간 문자열을 LocalTime 객체로 변환하는 메서드
    private LocalTime parseTime(String timeString) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            return LocalTime.parse(timeString, formatter);
        } catch (DateTimeParseException e) {
            // 유효하지 않은 시간 값 처리
            throw new IllegalArgumentException("유효하지 않은 시간 값: " + timeString);
        }
    }

    //스터디 모집 글 수정 = 스터디룸 정보 수정
    @Transactional
    public StudyResDto.StudyCreateRespDto modifyRoom(StudyReqDto.StudyModifyReqDto studyModifyReqDto) {

        // studyId로 Studyroom 조회
        StudyRoom studyRoom = studyRoomRepository.findById(studyModifyReqDto.getStudyId())
                .orElseThrow(() -> new CustomApiException("존재하지 않는 스터디 그룹입니다.")
                );

        // 현재 수정하는 사람이 스터디 조장인지 체크
        if (
                studyModifyReqDto.getCreatedBy() != studyRoom.getMaster().getId()
        ) {
            throw new CustomForbiddenException("조장만 스터디 그룹 정보를 수정 할 수 있습니다.");
        }


        // update()로 객체에 변경 사항 반영
        studyRoom.updateStudyRoom(studyModifyReqDto, parseTime(studyModifyReqDto.getStartTime())
                , parseTime(studyModifyReqDto.getEndTime()));


        studyRoom.getAttendanceDay().clear();
        attendanceDayRepository.deleteAttendanceDaysByStudyRoomId(studyModifyReqDto.getStudyId());


        //스터디룸과 연계된 출석일,기술 스택들 studyRoom 삽입
        for (String day : studyModifyReqDto.getAttendanceDay()) {
            AttendanceDay attendanceDay = AttendanceDay.builder()
                    .attendanceDay(day)
                    .studyRoom(studyRoom)
                    .build();

            studyRoom.getAttendanceDay().add(attendanceDay);
        }
        // StudyRoom의 AttendanceDay 정보를 String Set으로 변환
        Set<String> attendanceDays = Optional.ofNullable(studyRoom.getAttendanceDay())
                .orElseGet(Collections::emptySet)
                .stream()
                .map(AttendanceDay::getAttendanceDay)
                .collect(Collectors.toSet());

        //3.study_skill 테이블에 skill 삽입
        //3-1 Study_skill Entity 선언, Study_skill Entity에 데이터 집어 넣기, DB에 Insert
        // 기존의 스터디 룸과 관련된 모든 StudySkill 엔티티를 가져옴
        List<StudySkill> existingStudySkills = studySkillRepository.findByStudyRoomId(studyModifyReqDto.getStudyId());

        // 기존의 스킬을 모두 삭제
        studySkillRepository.deleteAll(existingStudySkills);

        // 새로운 스킬을 추가
        for (String skillName : studyModifyReqDto.getSkillNames()) {
            Skill skill = skillRepository.findBySkillName(skillName);

            if (skill != null) { // 스킬을 찾았을 때만 처리
                StudySkill studySkill = StudySkill.builder()
                        .studyRoom(studyRoom)
                        .skill(skill)
                        .build();
                studySkillRepository.save(studySkill);
            }
        }


        // studyRoom 기반으로 studyCreateRespDto 채우기
        StudyResDto.StudyCreateRespDto studyCreateRespDto = StudyResDto.StudyCreateRespDto.builder()
                .studyName(studyRoom.getStudyName())
                .createdAt(studyRoom.getCreatedAt())
                .description(studyRoom.getDescription())
                .endDate(studyRoom.getEndDate())
                .endTime(studyRoom.getEndTime())
                .startTime(studyRoom.getStartTime())
                .maxNum(studyRoom.getMaxNum())
                .startDate(studyRoom.getStartDate())
                .title(studyRoom.getTitle())
                .updatedAt(studyRoom.getUpdatedAt())
                .attendanceDay(attendanceDays) // Set<String>으로 변환한 정보를 저장
                .skillNames(studyModifyReqDto.getSkillNames())
                .userId(studyRoom.getMaster().getId())
                .build();

        return studyCreateRespDto;
    }



    // 민희 수정
    @Transactional
    public StudyResDto.StudyCreateRespDto createRoom(StudyReqDto.StudyCreateReqDto studyCreateReqDto) {

        // 스터디 이름 중복 검사
        if (studyRoomRepository.existsByStudyName(studyCreateReqDto.getStudyName())) {
            throw new CustomApiException("스터디 이름이 이미 존재합니다.");
        }
        // 1. 넘겨 받은 studyReqDto에서 정보 가져오기
        User master = userRepository.findById(studyCreateReqDto.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 문자열로 받은 시간 localTime 타입으로 파싱
        LocalTime startTime = parseTime(studyCreateReqDto.getStartTime());
        LocalTime endTime = parseTime(studyCreateReqDto.getEndTime());

        // StudyRoom 저장
        StudyRoom studyRoom = StudyRoom.builder()
                .studyName(studyCreateReqDto.getStudyName())
                .title(studyCreateReqDto.getTitle())
                .description(studyCreateReqDto.getDescription())
                .startDate(studyCreateReqDto.getStartDate())
                .endDate(studyCreateReqDto.getEndDate())
                .startTime(startTime)
                .endTime(endTime)
                .currentNum(1)
                .maxNum(studyCreateReqDto.getMaxNum())
                .master(master)
                .build();

        studyRoomRepository.save(studyRoom);

        // 연관관계 AttendanceDay 저장
        for (String day : studyCreateReqDto.getAttendanceDay()) {
            AttendanceDay attendanceDay = AttendanceDay.builder()
                    .attendanceDay(day)
                    .studyRoom(studyRoom)
                    .build();

            studyRoom.getAttendanceDay().add(attendanceDay);
        }


        //3.study_skill 테이블에 skill 삽입
        //3-1 Study_skill Entity 선언, Study_skill Entity에 데이터 집어 넣기, DB에 Insert
        // expertise
        for (String skillName : studyCreateReqDto.getSkillNames()) {

            Skill skill = skillRepository.findBySkillName(skillName); // 스킬 이름으로 Skill 엔티티 검색

            StudySkill studySkill = StudySkill.builder()
                    .studyRoom(studyRoom)
                    .skill(skill)
                    .build();
            studySkillRepository.save(studySkill);
        }

        // StudyRoom의 AttendanceDay 정보를String Set으로 변환
        Set<String> attendanceDays = Optional.ofNullable(studyRoom.getAttendanceDay())
                .orElseGet(Collections::emptySet)
                .stream()
                .map(AttendanceDay::getAttendanceDay)
                .collect(Collectors.toSet());


        // ResponseDto 생성
        StudyResDto.StudyCreateRespDto studyCreateRespDto = StudyResDto.StudyCreateRespDto.builder()
                .studyName(studyRoom.getStudyName())
                .createdAt(studyRoom.getCreatedAt())
                .description(studyRoom.getDescription())
                .endDate(studyRoom.getEndDate())
                .endTime(studyRoom.getEndTime())
                .startTime(studyRoom.getStartTime())
                .maxNum(studyRoom.getMaxNum())
                .startDate(studyRoom.getStartDate())
                .title(studyRoom.getTitle())
                .updatedAt(studyRoom.getUpdatedAt())
                .attendanceDay(attendanceDays) // Set<String>으로 변환한 정보를 저장
                .skillNames(studyCreateReqDto.getSkillNames())
                .userId(master.getId())
                .build();

        //4. Study_member에 만든 사람(조장) 추가 하기
        StudyMember studyMember = StudyMember.builder()
                .studyRoom(studyRoom)
                .user(master)
                .status(1)
                .build();

        studyMemberRepository.save(studyMember);

        return studyCreateRespDto;
    }


    // 반환 타입이 Long인지 확인하는 메소드
    public boolean checkReturnType(Object returnValue) {
        return returnValue instanceof Long;
    }

    //study 가입 신청
    @Transactional
    public StudyResDto.StudyRoomApplyResDto studyApply(StudyReqDto.StudyApplyReqDto studyApplyReqDto) {

        if (     // -1. userId,studyId를 기반으로 먼저 유저가 이미 가입한 상태인지 체크
                checkReturnType(studyRoomRepository.findIdByuserIdAndstudyId(studyApplyReqDto.getStudyId(),
                        studyApplyReqDto.getUserId()))
        ) {
            throw new CustomForbiddenException("이미 가입한 유저입니다.");
        }

        //0. DB에 저장할 스터디룸 엔티티를 studyId를 기반으로 가져와야 함.
        StudyRoom studyRoom;
        Optional<StudyRoom> optionalStudyRoom;

        optionalStudyRoom =
                studyRoomRepository.findById(studyApplyReqDto.getStudyId());
        studyRoom = optionalStudyRoom.orElse(null);

        // 1.DB에 저장할 User 엔티티를 userId를 기반으로 가져와야 함.
        Optional<User> optionalUser = userRepository.findById(studyApplyReqDto.getUserId());

        User user = optionalUser.orElse(null); // Provide a default value (null in this case)

        if (!(optionalUser.isPresent())) {         //User가 NUll인 경우
            throw new CustomForbiddenException("존재하지 않는 유저입니다.");
        } else if (!(optionalStudyRoom.isPresent())) { //스터디룸이 Null인 경우 -> 비어있는 스터디룸
            throw new CustomForbiddenException("존재하지 않는 스터디룸입니다.");
        }

        // 2. DB에 저장할 Study_Member Entity 선언,save
        StudyMember studyMember = StudyMember.builder()
                .studyRoom(studyRoom)
                .user(user)
                .status(0) //not approved
                .build();

        studyMemberRepository.save(studyMember);

        return new StudyResDto.StudyRoomApplyResDto(studyRoom.getId());

    }

    //스터디 모임 상세 조회 + 민희 수정 (출석요일 추가)
    @Transactional
    public StudyResDto.StudyRoomDetailResDto studyRoomDetailBrowse(Long studyId) {
        StudyRoom studyRoom = studyRoomRepository.findWithMasterAndSkillsById(studyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 스터디룸이 없습니다. id=" + studyId));

        log.info("studyId {}", studyId);

        List<StudySkill> studySkills = studySkillRepository.findByStudyRoomId(studyId);

        log.info("studySkills {} : ", studySkills);

        Set<AttendanceDay> attendanceDaySet = attendanceDayRepository.findByStudyRoomId(studyId);

        log.info("attendanceDaySet{}", attendanceDaySet);

        Set<String> attendanceDays = attendanceDaySet.stream()
                .map(AttendanceDay::getAttendanceDay)
                .collect(Collectors.toSet());

        log.info("attendanceDays: {}", attendanceDays);

        return new StudyResDto.StudyRoomDetailResDto(studyRoom, studySkills, attendanceDays);
    }

    // 스터디 탈퇴
    @Transactional
    public void withdrawStudy(Long userId, Long studyId) {
        // 1. 해당 스터디룸 정보 가져오기
        Optional<StudyRoom> studyRoom = studyRoomRepository.findById(studyId);

        if (studyRoom.isPresent()) {
            StudyRoom room = studyRoom.get();

            if (!(room.getMaster().getId() == userId)) {
                studyMemberRepository.deleteByUserIdAndStudyRoomId(userId, studyId);
            } else {
                throw new CustomApiException("스터디 장은 탈퇴가 불가능합니다. 권한을 양도한 후에 시도해 주시기 바랍니다.");
            }
        } else {
            throw new CustomApiException("스터디를 찾을 수 없습니다.");
        }
    }

    // 스터디 목록 불러오기
    @Transactional
    public List<StudyResDto.StudyListRespDto> mainList() {
        List<StudyRoom> studyRooms = studyRoomRepository.findAllWithMaster();
        return studyRooms.stream()
                .map((StudyResDto.StudyListRespDto::new))
                .collect(Collectors.toList());
    }


    // Study_skill 불러오기
    private List<StudySkill> getStudySkills(StudyRoom studyRoom) {
        return studySkillRepository.findByStudyRoomId(studyRoom.getId());
    }


    //관리자 스터디 그룹 관리 페이지에서 스터디 멤버 목록 조회
    public List<PostRespDto.StudyMemberListDto> postStudyMemberListInAdminPage(Long studyId) {

        List<StudyMember> studyMembers = studyMemberRepository.findApprovedMemberById(studyId);

        return studyMembers.stream()
                .map(PostRespDto.StudyMemberListDto::new)
                .collect(Collectors.toList());

        /*
        조회된 게시물은 List<StudyMember> 형태로 반환
        stream() 메서드를 사용하여 이 리스트를 스트림으로 변환합니다.
        스트림을 사용하면 데이터를 조작하고 변환할 수 있는 다양한 기능을 사용할 수 있습니다.

        .map(PostRespDto.StudyMemberListDto::new)
        map() 메서드를 사용하여 각 StudyMember를 PostRespDto.StudyMemberListDto로 반환. DTO로의 매핑 작업
        PostRespDto.StudyMemberListDto::new : StudyMemberListDto의 생성자를 이용해 StudyMember를 해당 DTO로 변환

        collect(Collectors.toList()):
        collect() 메서드를 사용하여 스트림의 요소들을 다시 리스트로 수집합니다.
        이 때, Collectors.toList()를 사용하여 스트림의 요소들을 리스트로 변환합니다.

        최종적으로 변환된 List<PostRespDto.StudyMemberListDto>가 반환됩니다.
        */

    }


    public List<StudyResDto.ApplicationResDto> applications(Long studyId) {

        return studyMemberRepository.applications(studyId);
    }

    public StudyResDto.ApplicationEssayResDto applicationsEssay(Long studyId, Long userId) {

        return studyMemberRepository.applicationsEssay(studyId, userId);
    }

    public StudyRoom findStudyRoomById(Long studyId) {
        return studyRoomRepository.findById(studyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 스터디룸을 찾을 수 없습니다."));
    }

    public Long findcreatedByBystudyId(Long studyId) {
        return studyRoomRepository.findcreatedByBystudyId(studyId);
    }

    @Transactional
    public boolean isUserInStudyRoom(Long userId, Long studyId) {
        Optional<StudyMember> studyMember = studyMemberRepository.findByUserAndStudyRoom(userId, studyId);
        return studyMember.isPresent();
    }


    // 스터디룸 참가 인원 리스트 조회
    public List<StudyMember> getStudyMembersByRoomId(Long studyId) {

        // 찬 : Study_Member Table에서 studyRoomId = ? And status = 1인 조건을 충족하는 List<StudyMember>를 Return하도록 수정
        return studyMemberRepository.findByStudyRoomId(studyId);

    }

    // 관리자 - 스터디룸 참가 인원 리스트 조회
    public List<StudyResDto.StudyMemberAndStatusListRespDto> getStudyMembersByRoomIdAsAdmin(Long studyId) {

        return studyMemberRepository.getStudyMembersByRoomIdAsAdmin(studyId);

    }

    // 스터디룸 멤버 강제 퇴출 + (찬:강제 퇴출하는 사람이 조장이 맞는지 체크하는 로직 추가)
    @Transactional
    public StudyMember deleteMember(Long userId, Long studyId, Long memberId) {

        if ((studyMemberRepository.findSpecificByUserId(userId, studyId)) == memberId) {
            throw new CustomForbiddenException("자기 자신을 퇴출 할 수 없습니다.");
        }

        //강제 퇴출하는 사람이 조장이 맞는지 체크
        if (userId != studyRoomRepository.findcreatedByBystudyId(studyId)) {
            throw new CustomForbiddenException("조장만 스터디 원을 퇴출 할 수 있습니다.");
        } else {
            StudyMember studyMember = studyMemberRepository.findById(memberId)
                    .orElseThrow(() -> new EntityNotFoundException("해당 memberId가 존재하지 않습니다." + memberId));

            notificationRepository.deleteByMemberId(memberId);
            studyMemberRepository.delete(studyMember);

            return studyMember;
        }
    }

    // 메인 제목 검색
    public List<StudyResDto.StudyListRespDto> findStudyRoomByKeyword(String keyword) {
        List<StudyRoom> studyRooms = studyRoomRepository.findStudyRoomByKeyword(keyword);

        if (studyRooms.isEmpty()) {
            throw new CustomApiException("해당 스터디가 존재하지 않습니다.");
        }

        return studyRooms.stream()
                .distinct()
                .map((StudyResDto.StudyListRespDto::new))
                .collect(Collectors.toList());
    }

    public StudyResDto.CheckStudyDateRespDto checkDate(Long studyId) {
        StudyRoom studyRoom = studyRoomRepository.findById(studyId)
                .orElseThrow(() -> new CustomApiException("해당 스터디 룸이 존재하지 않습니다."));

        boolean isEndDateToday = LocalDate.now().isEqual(studyRoom.getEndDate());

        return new StudyResDto.CheckStudyDateRespDto(studyRoom, isEndDateToday);
    }


    // 스터디 멤버 아이디 조회 메서드
    public Long findStudyMemberId(Long studyId, Long userId) {
        Long studyMemberId = studyMemberRepository.findByStudyRoomAndUser(studyId, userId);
        return studyMemberId;
    }

}
