package com.abo2.recode.domain.notification;

import com.abo2.recode.domain.studymember.StudyMember;
import com.abo2.recode.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
@Entity
@EnableJpaAuditing
public class Notifications {

    // PK
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    // FK
    // 사용자의 id 연관관계
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "study_member_id")
    private StudyMember studyMember;

    // FK
    // 알림 내용
    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private String messageId;

    // 알림 발생 시간
    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // 읽음 상태 (true / false)
    @Column(nullable = false)
    private boolean readStatus;

    // 알림 유형 (그룹 리더로서의 알림인지, 사용자 관점에서의 알림인지 구분)
    @Column(nullable = true)
    private String type;

    @Column(nullable = true)
    private boolean isNotified;  // 알림 동의 / 비동의

    @Builder
    public Notifications(Long id, User user, StudyMember studyMember,
                         String message, String messageId,
                         LocalDateTime createdAt, LocalDateTime updatedAt,
                         boolean readStatus, String type) {
        this.id = id;
        this.user = user;
        this.studyMember = studyMember;
        this.message = message;
        this.messageId = messageId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.readStatus = readStatus;
        this.type = type;
    }


    public void updateReadStatus(boolean readStatus) {
        this.readStatus = readStatus;
    }
}