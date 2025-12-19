package com.coelacanthe.repositories;

import com.coelacanthe.entities.MatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRepository extends JpaRepository<MatchEntity, Long> {

    Optional<MatchEntity>  findByExternalMatchId(String externalMatchId);

    List<MatchEntity> findAllByMatchDateBetweenAndPreMatchNotificationSentIsFalse(LocalDateTime start, LocalDateTime end);

    List<MatchEntity> findAllByMatchDateBeforeAndPreMatchNotificationSentIsTrue(LocalDateTime endDate);

    List<MatchEntity> findByMatchDate(LocalDateTime matchDate);

    List<MatchEntity> findAllByMatchDateBetween(Instant start, Instant end);

}
