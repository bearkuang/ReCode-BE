package com.abo2.recode.domain.estimate;

import com.abo2.recode.domain.studyroom.StudyRoom;
import com.abo2.recode.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class StudyMemberEstimate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_member_estimate_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "study_id")
    private StudyRoom studyRoom;

    @ManyToOne
    @JoinColumn(name = "target_id")
    private User user;

    private Integer totalPoint;

    @CreatedDate
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "studyMemberEstimate", cascade = CascadeType.ALL)
    private List<Estimate> estimates;
}
