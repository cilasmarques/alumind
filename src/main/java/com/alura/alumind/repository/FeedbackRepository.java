package com.alura.alumind.repository;

import com.alura.alumind.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    @Query("SELECT COUNT(f) FROM Feedback f WHERE f.sentiment = :sentiment")
    long countBySentiment(Feedback.SentimentType sentiment);

    @Query("SELECT COUNT(f) FROM Feedback f WHERE f.createdAt BETWEEN :start AND :end")
    long countByDate(LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(f) FROM Feedback f WHERE f.createdAt BETWEEN :start AND :end AND f.sentiment = :sentiment")
    long countByDateAndSentiment(LocalDateTime start, LocalDateTime end, Feedback.SentimentType sentiment);
}