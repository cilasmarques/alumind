package com.alura.alumind.repository;

import com.alura.alumind.dto.ReportResponse.TopFeaturesDto;
import com.alura.alumind.model.RequestedFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RequestedFeaturesRepository extends JpaRepository<RequestedFeature, Long> {

    @Query(value = """
                SELECT
                    rf.code as code,
                    STRING_AGG(DISTINCT CAST(f.id AS TEXT), ',') as feedbackIds,
                    COUNT(DISTINCT f.id) as feedbacksCounter
                FROM
                    requested_features rf
                JOIN
                    feedbacks f ON rf.feedback_id = f.id
                GROUP BY
                    rf.code
                ORDER BY
                    feedbacksCounter DESC
            """, nativeQuery = true)
    List<TopFeaturesDto> findRFWithFeedbackIds();

    @Query(value = """
                SELECT
                    rf.code as code,
                    STRING_AGG(DISTINCT CAST(f.id AS TEXT), ',') as feedbackIds,
                    COUNT(DISTINCT f.id) as feedbacksCounter
                FROM
                    requested_features rf
                JOIN
                    feedbacks f ON rf.feedback_id = f.id
                WHERE
                    f.created_at BETWEEN :startDate AND :endDate
                GROUP BY
                    rf.code
                ORDER BY
                    feedbacksCounter DESC
            """, nativeQuery = true)
    List<TopFeaturesDto> findRFWithFeedbackIdsForDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}