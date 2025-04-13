package com.alura.alumind.repository;

import com.alura.alumind.model.RequestedFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RequestedFeaturesRepository extends JpaRepository<RequestedFeature, Long> {

    @Query("SELECT rf.code, COUNT(rf) as count FROM RequestedFeature rf GROUP BY rf.code ORDER BY count DESC")
    List<Object[]> findMostRequestedFeatures();

    @Query("SELECT rf.code, COUNT(rf) as count, rf.reason FROM RequestedFeature rf " +
            "JOIN rf.feedback f " +
            "WHERE f.createdAt BETWEEN :start AND :end " +
            "GROUP BY rf.code, rf.reason ORDER BY count DESC")
    List<Object[]> findMostRequestedFeaturesBetweenDates(LocalDateTime start, LocalDateTime end);
}