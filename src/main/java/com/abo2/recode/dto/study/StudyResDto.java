package com.abo2.recode.dto.study;

import com.abo2.recode.domain.skill.StudySkill;
import com.abo2.recode.domain.studymember.StudyMember;
import com.abo2.recode.domain.studyroom.StudyRoom;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class StudyResDto {

    @Getter
    @Setter
    public static class StudyRoomDetailResDto{

//        {
//            "id": 1,
//                "study_name": "Study Group for XYZ",
//                "title": "Looking for members to study XYZ!",
//                "description": "We are a group of learners looking to deepen our understanding of XYZ. Join us!",
//                "start_date": "2023-12-01",
//                "end_date": "2024-01-01",
//                "max_num": 10,
//                "user_id": 42,
//                "createdAt": "2023-11-01T12:00:00Z",
//                "updatedAt": "2023-11-02T15:30:00Z"
//        }

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long study_room_id; //스터디 그룹 일련번호

        @Column(unique = true,nullable = false,length = 50)
        private String study_name; //스터디 그룹 네임

        @Column(nullable = false,length = 50)
        private String title; //스터디 주제

        @Column(nullable = false,length = 300)
        private String description; //스터디 그룹 소개글

        @Column(nullable = false)
        private LocalDate start_date; //스터디 시작 기간

        @Column(nullable = false)
        private LocalDate end_date; //스터디 마무리 기간

        @Column(nullable = false)
        private String start_time; //스터디 출석 인정 시작 시간

        @Column(nullable = false)
        private String end_time; //스터디 출석 인정 끝 시간

        @Column(nullable = false)
        private Integer current_num = 1; // 필드 선언 시 기본값 지정,스터디 그룹 현재 인원

        @Column(nullable = false)
        private Integer max_num; //스터디 그룹 전체 티오

        @Column(nullable = false)
        private String master; // 스터디 장

        private List<String> skillNames;

        @CreatedDate
        @Column(nullable = false)
        private LocalDateTime createdAt; //스터디 그룹 만든 시각

        @LastModifiedDate
        @Column(nullable = false)
        private LocalDateTime updatedAt; //스터디 그룹 수정 시각

        public StudyRoomDetailResDto(StudyRoom studyRoom, List<StudySkill> studySkills) {
            this.study_room_id = studyRoom.getId();
            this.study_name = studyRoom.getStudyName();
            this.title = studyRoom.getTitle();
            this.description = studyRoom.getDescription();
            this.start_date = studyRoom.getStartDate();
            this.end_date = studyRoom.getEndDate();
            this.start_time = String.valueOf(studyRoom.getStartTime());
            this.end_time = String.valueOf(studyRoom.getEndTime());
            this.current_num = studyRoom.getCurrentNum();
            this.max_num = studyRoom.getMaxNum();
            this.master = studyRoom.getMaster().getNickname();
            this.skillNames = studySkills.stream()
                    .map(studySkill -> studySkill.getSkill().getSkillName())
                    .collect(Collectors.toList());
            this.createdAt = studyRoom.getCreatedAt();
            this.updatedAt = studyRoom.getUpdatedAt();
        }

    }

    @Getter
    @Setter
    public static class MyStudyRespDto {
        private Long id;
        private String study_name;
        private String title;
        private String status;
        private List<String> skillNames;

        public MyStudyRespDto(StudyMember studyMember, List<StudySkill> studySkills) {
            StudyRoom studyRoom = studyMember.getStudyRoom();

            this.id = studyRoom.getId();
            this.study_name = studyRoom.getStudyName();
            this.title = studyRoom.getTitle();
            this.status = getStatusString(studyMember.getStatus());
            this.skillNames = studySkills.stream()
                    .map(studySkill -> studySkill.getSkill().getSkillName())
                    .collect(Collectors.toList());
        }

        private String getStatusString(Integer status) {
            switch (status) {
                case 0:
                    return "대기중";
                case 1:
                    return "열공중";
                case 2:
                    return "거절됨";
                default:
                    return "unknown";
            }
        }
    }

    @Getter
    @Setter
    public static class StudyListRespDto {
        private Long id;
        private String study_name;
        private String title;
        private List<String> skillNames;
        private Integer current_num;
        private Integer max_num;
        private String masterEmail;
        private String masterNickname;

        public StudyListRespDto(StudyRoom studyRoom, List<StudySkill> studySkills) {
            this.id = studyRoom.getId();
            this.study_name = studyRoom.getStudyName();
            this.title = studyRoom.getTitle();

            this.skillNames = studySkills.stream()
                    .map(studySkill -> studySkill.getSkill().getSkillName())
                    .collect(Collectors.toList());

            this.current_num = studyRoom.getCurrentNum();
            this.max_num = studyRoom.getMaxNum();
            if (studyRoom.getMaster() != null) {
                this.masterNickname = studyRoom.getMaster().getNickname();
                this.masterEmail = studyRoom.getMaster().getEmail();
            }
        }
    }

    @Getter
    @Setter
    public static class StudyRoomApplyResDto{
     /*   {
            "code": 1,
                "msg": "스터디 신청에 성공하였습니다.",
                "data": {
            "study_id": 1
        }
        }*/

        @NotEmpty
        private Long study_id;

        public StudyRoomApplyResDto(Long study_id) {
            this.study_id = study_id;
        }
    }


//    @Getter
//    @Setter
//    public static class StudyRoomCreateResDto{
//
//        private Long id;
//        private String study_name;
//        private String title;
//        private List<String> skillNames;
//        private Integer current_num;
//        private Integer max_num;
//        private String masterEmail;
//        private String masterNickname;
//
//    }

    @Getter
    @Setter
    public static class StudyCreateRespDto{

        // Study_Room
        @NotEmpty
        @Size(min = 1, max = 50)
        private String studyName;

        @NotEmpty
        @Size(min = 1, max = 50)
        private String title;

        @NotEmpty
        @Size(min = 1, max = 300)
        private String description;

        @NotEmpty
        private LocalTime startTime; //스터디 출석 인정 시작 시간 " 12:00"

        @NotEmpty
        private LocalTime endTime; //스터디 출석 인정 끝 시간 " 12:10"

        @NotEmpty
        private List<String> allowedDays; // 출석 인정 요일 - minhee 추가

        @NotEmpty
        private LocalDate startDate;

        @NotEmpty
        private LocalDate endDate;

        @NotEmpty
        private Integer maxNum;

        @NotEmpty
        private Long userId; // 별도로 가져오는 코드 필요(입력 받지 않음)

        @NotEmpty
        private LocalDateTime createdAt;

        @NotEmpty
        private LocalDateTime updatedAt;
        //======================================
        // skill 테이블의 스킬들,모집분야
        private String[] skills;

        @Builder
        public StudyCreateRespDto(String studyName, String title, String description,
                                  LocalTime startTime, LocalTime endTime, List<String> allowedDays,
                                  LocalDate startDate, LocalDate endDate, Integer maxNum, Long userId,
                                  LocalDateTime createdAt, LocalDateTime updatedAt, String[] skills) {
            this.studyName = studyName;
            this.title = title;
            this.description = description;
            this.startTime = startTime;
            this.endTime = endTime;
            this.allowedDays = allowedDays;
            this.startDate = startDate;
            this.endDate = endDate;
            this.maxNum = maxNum;
            this.userId = userId;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
            this.skills = skills;
        }
    }

}

