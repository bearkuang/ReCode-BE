package com.abo2.recode.domain.estimate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StudyMemberEstimateRepository extends JpaRepository<StudyMemberEstimate, Long> {

    @Query("SELECT sme FROM StudyMemberEstimate sme " +
            "WHERE sme.user.id = :userId " +
            "AND sme.studyRoom.id = :studyId")
    Optional<StudyMemberEstimate> findByUserIdAndStudyRoomId(@Param("userId") Long userId,
                                                             @Param("studyId") Long studyId);
}
