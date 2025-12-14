package com.coelacanthe.scheduler;

import com.coelacanthe.dto.NotificationRequest;
import com.coelacanthe.entities.MatchEntity;
import com.coelacanthe.events.MatchRegisteredEvent;
import com.coelacanthe.services.FirebaseMessagingService;
import com.coelacanthe.services.MatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Component
@Slf4j
@RequiredArgsConstructor
public class DynamicMatchNotificationScheduler {

    private final MatchService matchService;
    private final FirebaseMessagingService firebaseMessagingService;

    private final TaskScheduler taskScheduler;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    // Stockage des tâches planifiées pour pouvoir les annuler si nécessaire
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    /**
     * Cron qui s'exécute tous les jours à 00:01 pour planifier les notifications du jour
     */
    @Scheduled(cron = "0 1 0 * * *") // Tous les jours à 00:01
    public void scheduleDailyNotifications() {
        log.info("=== Planification des notifications pour la journée ===");
        scheduleNotificationsForToday();
    }

    /**
     * Au démarrage de l'application, planifier les notifications pour les matchs restants du jour
     */
    @EventListener(ApplicationReadyEvent.class)
    public void scheduleNotificationsOnStartup() {
        log.info("=== Démarrage de l'application : planification des notifications restantes du jour ===");
        scheduleNotificationsForToday();
    }

    /**
     * Planifier les notifications pour tous les matchs du jour
     */
    private void scheduleNotificationsForToday() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        List<MatchEntity> todayMatches = matchService.getMatchesBetweenDates(startOfDay, endOfDay);

        if (todayMatches.isEmpty()) {
            log.info("Aucun match prévu aujourd'hui");
            return;
        }

        log.info("Nombre de matchs trouvés pour aujourd'hui : {}", todayMatches.size());

        for (MatchEntity match : todayMatches) {
            scheduleNotificationsForMatch(match);
        }
    }

    /**
     * Planifier les notifications pré-match et post-match pour un match spécifique
     */
    private void scheduleNotificationsForMatch(MatchEntity match) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime matchDate = match.getMatchDate();

        // Planifier la notification pré-match (10 minutes avant)
        LocalDateTime preNotificationTime = matchDate.minusMinutes(10);
        if (!match.getPreMatchNotificationSent() && preNotificationTime.isAfter(now)) {
            schedulePreMatchNotification(match, preNotificationTime);
        } else if (!match.getPreMatchNotificationSent()) {
            log.warn("Notification pré-match pour le match {} vs {} est dans le passé ({})",
                    match.getHomeTeam(), match.getAwayTeam(), preNotificationTime);
        }

        // Planifier la notification post-match (10 minutes après la fin estimée)
        LocalDateTime postNotificationTime = matchDate.plusMinutes(90 + 10); // 90 min de match + 10 min
        if (!match.getPostMatchNotificationSent() && postNotificationTime.isAfter(now)) {
            schedulePostMatchNotification(match, postNotificationTime);
        } else if (!match.getPostMatchNotificationSent()) {
            log.warn("Notification post-match pour le match {} vs {} est dans le passé ({})",
                    match.getHomeTeam(), match.getAwayTeam(), postNotificationTime);
        }
    }

    /**
     * Planifier une notification pré-match
     */
    private void schedulePreMatchNotification(MatchEntity match, LocalDateTime scheduledTime) {
        String taskKey = "pre_" + match.getId();

        // Annuler la tâche si elle existe déjà
        cancelTask(taskKey);

        Instant instant = scheduledTime.atZone(ZoneId.systemDefault()).toInstant();

        ScheduledFuture<?> scheduledTask = taskScheduler.schedule(
            () -> sendPreMatchNotification(match),
            instant
        );

        scheduledTasks.put(taskKey, scheduledTask);

        log.info("Notification pré-match planifiée pour {} vs {} à {}",
                match.getHomeTeam(), match.getAwayTeam(), scheduledTime.format(TIME_FORMATTER));
    }

    /**
     * Planifier une notification post-match
     */
    private void schedulePostMatchNotification(MatchEntity match, LocalDateTime scheduledTime) {
        String taskKey = "post_" + match.getId();

        // Annuler la tâche si elle existe déjà
        cancelTask(taskKey);

        Instant instant = scheduledTime.atZone(ZoneId.systemDefault()).toInstant();

        ScheduledFuture<?> scheduledTask = taskScheduler.schedule(
            () -> sendPostMatchNotification(match),
            instant
        );

        scheduledTasks.put(taskKey, scheduledTask);

        log.info("Notification post-match planifiée pour {} vs {} à {}",
                match.getHomeTeam(), match.getAwayTeam(), scheduledTime.format(TIME_FORMATTER));
    }

    /**
     * Envoyer la notification pré-match
     */
    private void sendPreMatchNotification(MatchEntity match) {
        try {
            // Récupérer le match à jour depuis la DB
            MatchEntity freshMatch = matchService.getMatchById(match.getId());

            if (freshMatch.getPreMatchNotificationSent()) {
                log.info("Notification pré-match déjà envoyée pour le match {} vs {}",
                        match.getHomeTeam(), match.getAwayTeam());
                return;
            }

            String title = "Match imminent !";
            String body = String.format("%s vs %s commence dans 10 minutes ! (%s)",
                    freshMatch.getHomeTeam(),
                    freshMatch.getAwayTeam(),
                    freshMatch.getMatchDate().format(TIME_FORMATTER));

            Map<String, String> data = new HashMap<>();
            data.put("match_id", freshMatch.getId().toString());
            data.put("match_date", freshMatch.getMatchDate().toString());
            data.put("type", "pre_match");

            NotificationRequest notificationRequest = new NotificationRequest(title, body, null, data);
            firebaseMessagingService.sendNotificationToAll(notificationRequest);

            // Marquer comme envoyée
            matchService.markPreNotificationSent(freshMatch.getId());

            log.info("✓ Notification pré-match envoyée pour: {} vs {}",
                    freshMatch.getHomeTeam(), freshMatch.getAwayTeam());

            // Retirer de la liste des tâches planifiées
            scheduledTasks.remove("pre_" + match.getId());

        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de la notification pré-match pour le match {}",
                    match.getId(), e);
        }
    }

    /**
     * Envoyer la notification post-match
     */
    private void sendPostMatchNotification(MatchEntity match) {
        try {
            // Récupérer le match à jour depuis la DB
            MatchEntity freshMatch = matchService.getMatchById(match.getId());

            if (freshMatch.getPostMatchNotificationSent()) {
                log.info("Notification post-match déjà envoyée pour le match {} vs {}",
                        match.getHomeTeam(), match.getAwayTeam());
                return;
            }

            String title = "Match terminé !";
            String body = String.format("%s vs %s vient de se terminer. Consultez les résultats !",
                    freshMatch.getHomeTeam(),
                    freshMatch.getAwayTeam());

            Map<String, String> data = new HashMap<>();
            data.put("match_id", freshMatch.getId().toString());
            data.put("type", "post_match");

            NotificationRequest notificationRequest = new NotificationRequest(title, body, null, data);
            firebaseMessagingService.sendNotificationToAll(notificationRequest);

            // Marquer comme envoyée
            matchService.markPostNotificationSent(freshMatch.getId());

            log.info("✓ Notification post-match envoyée pour: {} vs {}",
                    freshMatch.getHomeTeam(), freshMatch.getAwayTeam());

            // Retirer de la liste des tâches planifiées
            scheduledTasks.remove("post_" + match.getId());

        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de la notification post-match pour le match {}",
                    match.getId(), e);
        }
    }

    /**
     * Annuler une tâche planifiée
     */
    private void cancelTask(String taskKey) {
        ScheduledFuture<?> existingTask = scheduledTasks.get(taskKey);
        if (existingTask != null && !existingTask.isDone()) {
            existingTask.cancel(false);
            scheduledTasks.remove(taskKey);
            log.debug("Tâche {} annulée", taskKey);
        }
    }

    /**
     * Écouter l'événement MatchRegisteredEvent pour planifier les notifications d'un nouveau match
     */
    @EventListener
    public void onMatchRegistered(MatchRegisteredEvent event) {
        MatchEntity match = event.getMatch();
        log.info("Événement reçu : nouveau match enregistré {} vs {}",
                match.getHomeTeam(), match.getAwayTeam());
        scheduleNotificationsForMatch(match);
    }
}
