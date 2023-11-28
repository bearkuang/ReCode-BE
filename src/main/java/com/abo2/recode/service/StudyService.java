package com.abo2.recode.service;

import com.abo2.recode.domain.skill.Skill;
import com.abo2.recode.domain.skill.SkillRepository;
import com.abo2.recode.domain.skill.StudySkill;
import com.abo2.recode.domain.skill.StudySkillRepository;
import com.abo2.recode.domain.studymember.StudyMember;
import com.abo2.recode.domain.studymember.StudyMemberRepository;
import com.abo2.recode.domain.studyroom.StudyRoomRepository;
import com.abo2.recode.domain.studyroom.StudyRoom;
import com.abo2.recode.domain.user.User;
import com.abo2.recode.domain.user.UserRepository;
import com.abo2.recode.dto.study.StudyReqDto;
import com.abo2.recode.dto.study.StudyResDto;
import com.abo2.recode.handler.ex.CustomApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudyService {

    private StudyRoomRepository studyRoomRepository;

    private StudySkillRepository studySkillRepository;

    private SkillRepository skillRepository;

    private UserRepository userRepository;

    private StudyMemberRepository studyMemberRepository;

    @Autowired
    public StudyService(StudyRoomRepository studyRoomRepository,
                        StudySkillRepository studySkillRepository,
                        SkillRepository skillRepository,
                        UserRepository userRepository,
                        StudyMemberRepository studyMemberRepository) {
        this.studyRoomRepository = studyRoomRepository;
        this.studySkillRepository = studySkillRepository;
        this.skillRepository = skillRepository;
        this.userRepository = userRepository;
        this.studyMemberRepository = studyMemberRepository;
    }



    // 민희 수정
    // 요일 문자열을 enum 타입으로 변환하는 메서드
    private DayOfWeek parseDayOfWeek(String dayOfWeekString) {
        try {
            return DayOfWeek.valueOf(dayOfWeekString);
        } catch (IllegalArgumentException e) {
            // 유효하지 않은 요일 값 처리
            throw new IllegalArgumentException("유효하지 않은 요일 값: " + dayOfWeekString);
        }
    }



    // 민희 수정
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

    // 민희 수정
    public StudyResDto.StudyCreateRespDto createRoom(StudyReqDto.StudyCreateReqDto studyCreateReqDto) {

        // 1. 넘겨 받은 studyReqDto에서 정보 가져오기
        User master = userRepository.findById(studyCreateReqDto.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        LocalTime startTime = parseTime(studyCreateReqDto.getStartTime());
        LocalTime endTime = parseTime(studyCreateReqDto.getEndTime());

        StudyRoom studyRoom = StudyRoom.builder()
                .studyName(studyCreateReqDto.getStudyName())
                .title(studyCreateReqDto.getTitle())
                .description(studyCreateReqDto.getDescription())
                .startDate(studyCreateReqDto.getStartDate())
                .endDate(studyCreateReqDto.getEndDate())
                .startTime(startTime)
                .endTime(endTime)
                .currentNum(1)
                .allowedDays(studyCreateReqDto.getAllowedDays())
                .maxNum(studyCreateReqDto.getMaxNum())
                .master(master)
                .build();

        studyRoomRepository.save(studyRoom);

        // 3. study_skill 테이블에 skill 삽입
        // 3-1. Study_skill Entity 선언, Study_skill Entity에 데이터 집어 넣기, DB에 Insert
        // expertise
        for (String skillName : studyCreateReqDto.getSkills()) {
            Skill skill = skillRepository.findBySkillName(skillName); // 스킬 이름으로 Skill 엔티티 검색

            StudySkill studySkill = StudySkill.builder()
                    .studyRoom(studyRoom)
                    .skill(skill)
                    .build();

            studySkillRepository.save(studySkill);
        }

        StudyResDto.StudyCreateRespDto studyCreateRespDto = StudyResDto.StudyCreateRespDto.builder()
                .studyName(studyRoom.getStudyName())
                .createdAt(studyRoom.getCreatedAt())
                .allowedDays(studyRoom.getAllowedDays())
                .description(studyRoom.getDescription())
                .endDate(studyRoom.getEndDate())
                .endTime(studyRoom.getEndTime())
                .startTime(studyRoom.getStartTime())
                .maxNum(studyRoom.getMaxNum())
                .startDate(studyRoom.getStartDate())
                .title(studyRoom.getTitle())
                .updatedAt(studyRoom.getUpdatedAt())
                .build();

        return studyCreateRespDto;
    }

    // 문자열을 LocalTime으로 파싱하는 메서드

    // 문자열로부터 LocalDateTime으로 변환하는 메서드
//    private LocalDateTime parseToLocalDateTime(String dateTimeStr) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // 포맷 지정
//        return LocalDateTime.parse(dateTimeStr, formatter); // 파싱
//    }




//    public void createRoom(StudyReqDto.StudyCreateReqDto studyCreateReqDto){
//
//        System.out.println("Service createRoom()");
//
//        //1. 넘겨 받은 studyReqDto에서 정보 가져오기
//        User master = userRepository.findById(studyCreateReqDto.getUserId())
//                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
//
//        String study_name = studyCreateReqDto.getStudyName();
//        String title = studyCreateReqDto.getTitle();
//        String description = studyCreateReqDto.getDescription();
//        LocalDate start_date = studyCreateReqDto.getStartDate();
//        LocalDate end_date = studyCreateReqDto.getEndDate();
//        Integer current_num = 1; //스터디 그룹 현재 인원은 기본 1명으로 설정
//        Integer max_num = studyCreateReqDto.getMaxNum();
//
//        //1-1. start_time,end_time String -> LocalDateTime
//        LocalDateTime startDateTime = convertToDateTime(studyCreateReqDto.getStartTime());
//        LocalDateTime endDateTime = convertToDateTime(studyCreateReqDto.getEndTime());
//
//        //2. DB에 전송할 studyRoom Entity 선언, studyRoom Entity에 데이터 집어 넣기, DB에 Insert
//        StudyRoom studyRoom = StudyRoom.builder()
//                .studyName(study_name)
//                .title(title)
//                .description(description)
//                .startTime(startDateTime)
//                .endTime(endDateTime)
//                .startDate(start_date)
//                .endDate(end_date)
//                .currentNum(current_num)
//                .maxNum(max_num)
//                .master(master)
//                .build();
//
//        studyRoomRepository.save(studyRoom);
//
//        //3.study_skill 테이블에 skill 삽입
//        //3-1 Study_skill Entity 선언, Study_skill Entity에 데이터 집어 넣기, DB에 Insert
//        // expertise
//        for (String skillName : studyCreateReqDto.getSkills()) {
//            Skill skill = skillRepository.findBySkillName(skillName); // 스킬 이름으로 Skill 엔티티 검색
//
//            StudySkill studySkill = StudySkill.builder()
//                    .studyRoom(studyRoom)
//                    .skill(skill)
//                    .build();
//            studySkillRepository.save(studySkill);
//        }
//        System.out.println("Service createRoom() save !!!!!!");
//
//    } //createRoom()

    // 문자열을 LocalDateTime 객체로 변환
    private LocalDateTime convertToDateTime(String dateTimeStr) {
        String[] parts = dateTimeStr.split(" ");
        DayOfWeek dayOfWeek = DayOfWeek.valueOf(parts[0].toUpperCase());
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sameWeekDay = now.with(TemporalAdjusters.nextOrSame(dayOfWeek));
        LocalDateTime time = LocalDateTime
                .parse(sameWeekDay.format(DateTimeFormatter.ISO_LOCAL_DATE)
                + "T" + parts[1], DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        return time;
    }//convertToDateTime()

    //study 가입 신청
    public StudyResDto.StudyRoomApplyResDto studyApply(StudyReqDto.StudyApplyReqDto studyApplyReqDto) {

        //  studyApplyReqDto
//        @NotEmpty
//        Long study_id;
//
//        @NotEmpty
//        Long user_id;

        //0. DB에 저장할 스터디룸 엔티티를 study_id를 기반으로 가져와야 함.
        StudyRoom studyRoom = new StudyRoom();
        Optional<StudyRoom> optionalStudyRoom = Optional.of(studyRoom);

        optionalStudyRoom =
                studyRoomRepository.findById(studyApplyReqDto.getUser_id());
        studyRoom = optionalStudyRoom.orElse(null);

        // 1.DB에 저장할 User 엔티티를 User_id를 기반으로 가져와야 함.
        Optional<User> optionalUser = userRepository.findById(studyApplyReqDto.getUser_id());

        User user = optionalUser.orElse(null); // Provide a default value (null in this case)

        // 2. DB에 저장할 Study_Member Entity 선언,save
        StudyMember studyMember = StudyMember.builder()
                .studyRoom(studyRoom)
                .user(user)
                .status(0) //not approved
                .build();

        studyMemberRepository.save(studyMember);

        return new StudyResDto.StudyRoomApplyResDto(studyRoom.getId());
    }

    //스터디 모임 상세 조회
    @Transactional
    public StudyResDto.StudyRoomDetailResDto studyRoomDetailBrowse(Long study_room_id) {
        StudyRoom studyRoom = studyRoomRepository.findWithMasterAndSkillsById(study_room_id)
                .orElseThrow(() -> new IllegalArgumentException("해당 스터디룸이 없습니다. id=" + study_room_id));

        List<StudySkill> studySkills = studySkillRepository.findByStudyRoomId(study_room_id);
        return new StudyResDto.StudyRoomDetailResDto(studyRoom, studySkills);
    }

    // 스터디 탈퇴
    @Transactional
    public void withdrawStudy(Long userId, Long study_room_id){
        // 1. 해당 스터디룸 정보 가져오기
        Optional<StudyRoom> studyRoom = studyRoomRepository.findById(study_room_id);

        if(studyRoom.isPresent()) {
            StudyRoom room = studyRoom.get();

            if(!room.getMaster().equals(userId)){
                studyMemberRepository.deleteByUserIdAndStudyRoomUd(userId, study_room_id);
            } else {
                throw new CustomApiException("스터디 장은 탈퇴가 불가능합니다. 권한을 양도한 후에 시도해 주시기 바랍니다.");
            }
        } else {
            throw new CustomApiException("스터디를 찾을 수 없습니다.");
        }
    }

    // 스터디 목록 불러오기
    @Transactional
    public List<StudyResDto.StudyListRespDto> mainList(){
        List<StudyRoom> studyRooms = studyRoomRepository.findAllWithMaster();
        return studyRooms.stream()
                .map(studyRoom -> new StudyResDto.StudyListRespDto(studyRoom, getStudySkills(studyRoom)))
                .collect(Collectors.toList());
    }

    private List<StudySkill> getStudySkills(StudyRoom studyRoom) {
        return studySkillRepository.findByStudyRoomId(studyRoom.getId());
    }
}//class StudyService
