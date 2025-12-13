package com.coelacanthe.services;

import com.coelacanthe.dto.FcmTokenRequest;
import com.coelacanthe.dto.FcmTokenResponse;
import com.coelacanthe.entities.FcmTokenEntity;
import com.coelacanthe.repositories.FcmTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FcmTokenService {

    private final FcmTokenRepository fcmTokenRepository;

    /**
     * Enregistrer ou mettre à jour un token FCM
     */

    @Transactional
    public FcmTokenResponse registerToken(FcmTokenRequest request) {
        log.info("Enregistrement du token FCM: {}", request.token());

        Optional<FcmTokenEntity> existingToken = fcmTokenRepository.findByToken(request.token());
        FcmTokenEntity fcmToken;
        if (existingToken.isPresent()) {
            fcmToken = existingToken.get();
            fcmToken.setDeviceInfo(request.deviceInfo());
            fcmToken.setIsActive(true);
            log.info("Token existant mis à jour");
        }else{
            fcmToken = new FcmTokenEntity();
            fcmToken.setToken(request.token());
            fcmToken.setDeviceInfo(request.deviceInfo());
            fcmToken.setIsActive(true);
            log.info("Nouveau token créé");
        }
        fcmToken = fcmTokenRepository.save(fcmToken);
        return mapToResponseDto(fcmToken);
    }

    /**
     * Désactiver un token (quand l'app est désinstallée ou l'user se déconnecte)
     */

    public void deactivateToken(String token) {
        log.info("Désactivation du token: {}", token.substring(0, 20) + "...");
        int updated = fcmTokenRepository.deactivateToken(token);
        if(updated == 0) {
            log.info("Token non présent");
        }

    }

    /**
     * Récupérer tous les tokens actifs
     */
    public List<String> getAllActiveTokens() {
        log.info("Récupération de tous les tokens actifs");
        return fcmTokenRepository.findAllByIsActiveTrue()
                .stream()
                .map(FcmTokenEntity::getToken)
                .collect(Collectors.toList());
    }

    /**
     * Mapper l'entité vers le DTO de réponse
     */
    // A refactorer avec mapStruct
    private FcmTokenResponse mapToResponseDto(FcmTokenEntity fcmToken) {
        return new FcmTokenResponse(
                fcmToken.getId(),
                fcmToken.getToken(),
                fcmToken.getDeviceInfo(),
                fcmToken.getIsActive(),
                fcmToken.getCreatedDate(),
                fcmToken.getUpdatedDate()
        );
    }

    //

}
