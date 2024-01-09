package com.abo2.recode.domain.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface NotificationRepository extends JpaRepository<Notifications, Long> {

    @Query(value = "DELETE FROM Notifications WHERE study_member_id=:memberId",nativeQuery = true)
    void deleteByMemberId(@Param(value = "memberId") Long memberId);

    // 사용자의 알림 조회
    List<Notifications> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

}
