package com.coelacanthe.services;

import com.coelacanthe.dto.MatchRequest;
import com.coelacanthe.dto.MatchResponse;
import com.coelacanthe.entities.MatchEntity;
import com.coelacanthe.repositories.MatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;

    @Transactional
    public MatchResponse registerMatch(MatchRequest matchRequest) {
        MatchEntity match ;

        log.info("Enregistrement du match: {} vs {}", matchRequest.homeTeam(), matchRequest.awayTeam());

        if(matchRequest.externalId() != null){
            Optional<MatchEntity> existingMatch = matchRepository.findByExternalMatchId(matchRequest.externalId());

            if (existingMatch.isPresent()) {
                match = existingMatch.get();
                log.info("Match existant trouvé, mise à jour");
            } else {
                match = new MatchEntity();
                log.info("Nouveau match créé");
            }

        }else {
            match = new MatchEntity();
            log.info("Nouveau match créé (sans externalMatchId)");
        }
        // Mapper les données
        match.setHomeTeam(matchRequest.homeTeam());
        match.setAwayTeam(matchRequest.awayTeam());
        match.setMatchDate(matchRequest.matchDate());
        match.setCompetition(matchRequest.competition());
        match.setLieu(matchRequest.lieu());
        match.setExternalMatchId(matchRequest.externalId());

        match = matchRepository.save(match);

        return mapToResponseDto(match);
    }

    /**
            * Récupérer les matchs nécessitant une notification pré-match
     */
    public List<MatchEntity> getMatchesNeedingPreNotification() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tenMinutesFromNow = now.plusMinutes(10);
        LocalDateTime elevenMinutesFromNow = now.plusMinutes(11);
        return matchRepository.findAllByMatchDateBetweenAndPreMatchNotificationSentIsFalse(tenMinutesFromNow,elevenMinutesFromNow );
    }

    /**
            * Récupérer les matchs nécessitant une notification post-match
     */
    public List<MatchEntity> getMatchesNeedingPostNotification() {
        LocalDateTime now = LocalDateTime.now();
        return matchRepository.findAllByMatchDateBeforeAndPreMatchNotificationSentIsTrue(now.minusMinutes(10));
    }

    /**
     * Marquer la notification pré-match comme envoyée
     */
    @Transactional
    public void markPreNotificationSent(Long matchId) {
        matchRepository.findById(matchId).ifPresent(match -> {
            match.setPreMatchNotificationSent(true);
            matchRepository.save(match);
            log.info("Notification pré-match marquée comme envoyée pour le match {}", matchId);
        });
    }

    /**
     * Marquer la notification post-match comme envoyée
     */
    @Transactional
    public void markPostNotificationSent(Long matchId) {
        matchRepository.findById(matchId).ifPresent(match -> {
            match.setPostMatchNotificationSent(true);
            matchRepository.save(match);
            log.info("Notification post-match marquée comme envoyée pour le match {}", matchId);
        });
    }



    private MatchResponse mapToResponseDto(MatchEntity match) {
        return new MatchResponse(
                match.getId(),
                match.getHomeTeam(),
                match.getAwayTeam(),
                match.getMatchDate(),
                match.getCompetition(),
                match.getLieu(),
                match.getPreMatchNotificationSent(),
                match.getPostMatchNotificationSent());
    }

}
