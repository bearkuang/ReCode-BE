package com.abo2.recode.domain.user;

import com.abo2.recode.domain.estimate.StudyMemberEstimate;
import com.abo2.recode.domain.notification.Notifications;
import com.abo2.recode.domain.studymember.StudyMember;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Entity
@ToString
@Table(name = "users")
public class User {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(unique = true, nullable = false, length = 20)
    private String username;

    @Column(unique = true, nullable = false, length = 20)
    private String nickname;

    @Column(nullable = false, length = 60)
    private String password;

    @Column(length = 100)
    private String essay;

    @Column(nullable = false, length = 30)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserEnum role;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // 이메일 관련
    @Column(name = "email_check_token", nullable = true)
    private String emailCheckToken;

    @Column(name = "email_token_expiry", nullable = true)
    private LocalDateTime emailTokenExpiry;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<StudyMemberEstimate> studyMemberEstimates;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Notifications> notifications;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<StudyMember> studyMembers;

    @Builder
    public User(Long id, String username, String nickname, String password, String essay, String email, UserEnum role, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.password = password;
        this.essay = essay;
        this.email = email;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void updateUser(String nickname, String email) {
        this.email = email;
        this.nickname = nickname;
    }

    public void writeEssay(String essay) {
        this.essay = essay;
    }


    public void completeSignUp() {
        this.email = email;
        this.createdAt = createdAt;
    }

    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
        this.emailTokenExpiry = LocalDateTime.now().plusHours(1);
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public void changeProfile(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }
}