package com.coelacanthe.services;

import com.coelacanthe.dto.NotificationRequest;
import com.coelacanthe.dto.NotificationResponse;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FirebaseMessagingService {

    private final FcmTokenService fcmTokenService;

    private static final int BATCH_SIZE = 500;

    /**
     * Envoyer une notification à tous les utilisateurs actifs
     */

    public NotificationResponse sendNotificationToAll(NotificationRequest notificationRequest) {
        log.info("Envoi de notification à tous les utilisateurs: {}", notificationRequest.titre());

        List<String> tokens = fcmTokenService.getAllActiveTokens();

        if (tokens.isEmpty()) {
            log.warn("Aucun token actif trouvé");
            return new NotificationResponse(0, 0, "Aucun destinataire disponible");
        }

        return sendMulticastNotification(tokens, notificationRequest);
    }

    /**
            * Envoyer une notification à plusieurs tokens (multicast)
     */
    public NotificationResponse sendMulticastNotification(List<String> tokens,
                                                             NotificationRequest notificationRequest) {

        log.info("Envoi de notification multicast à {} tokens", tokens.size());
        int totalSuccess = 0;
        int totalFailure = 0;

        List<List<String>> batches = partitionList(tokens, BATCH_SIZE);


        for(List<String> batch : batches) {
            try{
                MulticastMessage message = buildMulticastMessage(batch, notificationRequest);
                BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);

                totalSuccess +=  response.getSuccessCount();
                totalFailure +=  response.getFailureCount();

                //Gerer les token invalides

                handleFailedTokens(batch, response);

                log.info("Batch envoyé - Succès: {}, Échecs: {}",
                        response.getSuccessCount(), response.getFailureCount());

            }catch(FirebaseMessagingException  e){
                log.error("Erreur lors de l'envoi du batch: {}", e.getMessage(), e);
                totalFailure += batch.size();
            }
        }

        String message = String.format("Notification envoyée - Succès: %d, Échecs: %d",
                totalSuccess, totalFailure);

        log.info(message);

        return new NotificationResponse(totalSuccess, totalFailure, message);
    }

    /**
     * Construire un message Firebase pour multicast
     */
    private MulticastMessage buildMulticastMessage(List<String> tokens,
                                                   NotificationRequest notificationRequest) {

        MulticastMessage.Builder builder = MulticastMessage.builder()
                .addAllTokens(tokens)
                .setNotification(Notification.builder()
                        .setTitle(notificationRequest.titre())
                        .setBody(notificationRequest.message())
                        .setImage(notificationRequest.imageUrl())
                        .build())
                .setAndroidConfig(AndroidConfig.builder()
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .setNotification(AndroidNotification.builder()
                                .setSound("default")
                                .setColor("#0080FF")
                                .build())
                        .build());
        // Ajouter des données additionnelles si présentes
        if (notificationRequest.data()!= null && !notificationRequest.data().isEmpty()) {
            builder.putAllData(notificationRequest.data());
        }

        return builder.build();

    }

    private void  handleFailedTokens(List<String> tokens, BatchResponse response) {
        List<SendResponse> responses = response.getResponses();

        for (int i = 0; i < responses.size(); i++) {
            SendResponse sendResponse = responses.get(i);

            if (!sendResponse.isSuccessful()) {
                String token = tokens.get(i);
                FirebaseMessagingException exception = sendResponse.getException();

                if(exception != null) {
                    MessagingErrorCode errorCode = exception.getMessagingErrorCode();
                    // Désactiver les tokens invalides
                    if (errorCode == MessagingErrorCode.UNREGISTERED ||
                            errorCode == MessagingErrorCode.INVALID_ARGUMENT) {
                        log.warn("Token invalide détecté, désactivation: {}", token);
                        fcmTokenService.deactivateToken(token);
                    }
                }
            }
        }
    }

    /**
            * Diviser une liste en batches
     */
    private <T> List<List<T>> partitionList(List<T> list, int batchSize) {
        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            partitions.add(list.subList(i, Math.min(i + batchSize, list.size())));
        }
        return partitions;
    }
}
