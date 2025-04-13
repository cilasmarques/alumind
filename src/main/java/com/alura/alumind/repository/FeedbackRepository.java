package com.alura.alumind.repository;

import com.alura.alumind.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    @Query("SELECT COUNT(f) FROM Feedback f WHERE f.sentiment = :sentiment")
    long countBySentiment(Feedback.SentimentType sentiment);

    @Query("SELECT f FROM Feedback f WHERE f.createdAt BETWEEN :start AND :end")
    List<Feedback> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}