package com.abo2.recode.domain.studymember;

import com.abo2.recode.domain.studyroom.StudyRoom;
import com.abo2.recode.domain.user.User;
import com.abo2.recode.dto.study.StudyResDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@NamedNativeQuery(
        name = "getApplicationEssayResDto",
        query = "SELECT u.username, u.email, u.essay " +
                "FROM Study_Member as sm " +
                "INNER JOIN Users as u " +
                "ON u.user_id = sm.user_id " +
                "WHERE sm.status = 0 AND sm.study_room_id = :studyId AND u.user_id = :userId",
        resultSetMapping = "ApplicationEssayResDtoMapping"
)
@SqlResultSetMapping(
        name = "ApplicationEssayResDtoMapping",
        classes = @ConstructorResult(
                targetClass = StudyResDto.ApplicationEssayResDto.class,
                columns = {
                        @ColumnResult(name = "username", type = String.class),
                        @ColumnResult(name = "email", type = String.class),
                        @ColumnResult(name = "essay", type = String.class)
                }
        )
)

@NamedNativeQuery(
        name = "getApplicationResDto",
        query = "SELECT sm.user_id as userId,u.username,sm.status,u.email " +
                "FROM Study_Member as sm " +
                "LEFT OUTER JOIN Users as u ON sm.user_id = u.user_id " +
                "WHERE sm.study_room_id=:studyId AND sm.status = 0",
        resultSetMapping = "ApplicationResDtoMapping"
)

@SqlResultSetMapping(
        name = "ApplicationResDtoMapping",
        classes = @ConstructorResult(
                targetClass = StudyResDto.ApplicationResDto.class,
                columns = {
                        @ColumnResult(name = "userId", type = Long.class),
                        @ColumnResult(name = "username", type = String.class),
                        @ColumnResult(name = "status", type = Integer.class),
                        @ColumnResult(name = "email", type = String.class)
                }
        )
)

@NamedNativeQuery(
        name = "StudyMemberAndStatusListRespDto",
        query = "SELECT sm.study_room_id as studyId,sm.user_id as userId,u.username,sr.created_by as createdBy " +
                "FROM Study_Member as sm " +
                "INNER JOIN Users as u ON sm.user_id = u.user_id " +
                "INNER JOIN Study_Room as sr ON sm.study_room_id = sr.study_room_id " +
                "WHERE sm.study_room_id=:studyId AND sm.status = 1",
        resultSetMapping = "StudyMemberAndStatusListRespDtoMapping"
)

@SqlResultSetMapping(
        name = "StudyMemberAndStatusListRespDtoMapping",
        classes = @ConstructorResult(
                targetClass = StudyResDto.StudyMemberAndStatusListRespDto.class,
                columns = {
                        @ColumnResult(name = "userId", type = Long.class),
                        @ColumnResult(name = "studyId", type = Long.class),
                        @ColumnResult(name = "username", type = String.class),
                        @ColumnResult(name = "createdBy", type = Integer.class)
                }
        )
)

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
public class StudyMember {

    @Id
    @Column(name = "study_member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //스터디 룸 member 일련번호

    @ManyToOne
    @JoinColumn(name = "study_room_id")
    private StudyRoom studyRoom;

    @ManyToOne(cascade = CascadeType.REMOVE) // or CascadeType.ALL
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 50)
    private Integer status;

    @Builder
    public StudyMember(StudyRoom studyRoom, User user, Integer status) {
        this.studyRoom = studyRoom;
        this.user = user;
        this.status = status;
    }

}