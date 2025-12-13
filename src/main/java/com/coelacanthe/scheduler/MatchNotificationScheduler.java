package com.coelacanthe.scheduler;

import com.coelacanthe.dto.NotificationRequest;
import com.coelacanthe.entities.MatchEntity;
import com.coelacanthe.services.FirebaseMessagingService;
import com.coelacanthe.services.MatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class MatchNotificationScheduler {

    private final MatchService matchService;

    private final FirebaseMessagingService firebaseMessagingService;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Vérifier toutes les minutes s'il y a des notifications à envoyer
     */
    //@Scheduled(cron = "0 * * * * *") // Toutes les minutes
    public void checkAndSendNotifications() {

        log.info("Vérification des notifications à envoyer...");

        sendPreMatchNotifications();
        sendPostMatchNotifications();
    }

    /**
     * Envoyer les notifications pré-match (10 min avant)
     */
    private void sendPreMatchNotifications() {
        List<MatchEntity> matches = matchService.getMatchesNeedingPreNotification();

        if(matches.isEmpty()){
            return;
        }

        log.info("Envoi de {} notification(s) pré-match", matches.size());

        for (MatchEntity match : matches) {
            try {
                String title = "Match imminent !";
                String body = String.format("%s vs %s commence dans 10 minutes ! (%s)",
                        match.getHomeTeam(),
                        match.getAwayTeam(),
                        match.getMatchDate().format(TIME_FORMATTER));

                Map<String, String> data = new HashMap<>();
                data.put("match_id", match.getId().toString());
                data.put("match_date", match.getMatchDate().toString());
                data.put("type", "pre_match");

                NotificationRequest notificationRequest = new NotificationRequest(title, body, null, data);

                firebaseMessagingService.sendNotificationToAll(notificationRequest);

                // Marquer comme envoyée
                matchService.markPreNotificationSent(match.getId());

                log.info("Notification pré-match envoyée pour: {} vs {}",
                        match.getHomeTeam(), match.getAwayTeam());
            } catch (Exception e) {
                log.error("Erreur lors de l'envoi de la notification pré-match pour le match {}",
                        match.getId(), e);
            }
        }


    }

    /**
     * Envoyer les notifications post-match (10 min après la fin estimée)
     */
    private void sendPostMatchNotifications() {
        List<MatchEntity> matches = matchService.getMatchesNeedingPostNotification();

        if (matches.isEmpty()) {
            return;
        }
        log.info("Envoi de {} notification(s) post-match", matches.size());

        for (MatchEntity match : matches) {
            try {
                LocalDateTime estimatedEndTime = match.getMatchDate()
                        .plusMinutes(90);
                // Vérifier si 10 minutes se sont écoulées depuis la fin
                if (LocalDateTime.now().isAfter(estimatedEndTime.plusMinutes(10))) {
                    String title = "Match terminé !";
                    String body = String.format("%s vs %s vient de se terminer. Consultez les résultats !",
                            match.getHomeTeam(),
                            match.getAwayTeam());
                    Map<String, String> data = new HashMap<>();
                    data.put("match_id", match.getId().toString());
                    data.put("type", "post_match");

                    NotificationRequest notificationRequest = new NotificationRequest(title, body, null, data);

                    firebaseMessagingService.sendNotificationToAll(notificationRequest);

                    // Marquer comme envoyée
                    matchService.markPostNotificationSent(match.getId());

                    log.info("Notification post-match envoyée pour: {} vs {}",
                            match.getHomeTeam(), match.getAwayTeam());
                }
            }catch (Exception e){
                log.error("Erreur lors de l'envoi de la notification post-match pour le match {}",
                        match.getId(), e);
            }
        }
    }

}
