package com.coelacanthe.services;

import com.coelacanthe.entities.enums.AuthProvider;
import com.coelacanthe.dto.AuthResponse;
import com.coelacanthe.dto.GoogleTokenRequest;
import com.coelacanthe.dto.UserInfo;
import com.coelacanthe.entities.UserEntity;
import com.coelacanthe.repositories.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;

    private final JwtService jwtService;

    @Value("${google.client-id}")
    private String googleClientId;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    public AuthResponse authenticateWithGoogle(GoogleTokenRequest request)  {
        try{
            // Vérifier le token Google
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(request.idToken());

            if (idToken == null) {
                throw new RuntimeException("Invalid Google ID token");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();

            // Extraire les informations de l'utilisateur
            String googleId = payload.getSubject();
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");

            // Trouver ou créer l'utilisateur
            UserEntity user = userRepository.findByGoogleId(googleId)
                    .orElseGet(() -> createNewUser(googleId, email, name, pictureUrl));

            // Générer le JWT
            String token = jwtService.generateToken(user.getEmail(), user.getId());

            AuthResponse authResponse = new AuthResponse(token,
                    "Bearer",
                    jwtExpiration / 1000,
                    new UserInfo(user.getId(),
                            user.getEmail(),
                            user.getName(),
                            user.getPictureUrl()));


            return authResponse;

        } catch (Exception e) {
            log.error("Error during Google authentication", e);
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }

    }

    private UserEntity createNewUser(String googleId, String email, String name, String pictureUrl) {
        UserEntity newUser = new UserEntity();
        newUser.setGoogleId(googleId);
        newUser.setEmail(email);
        newUser.setName(name);
        newUser.setPictureUrl(pictureUrl);
        newUser.setAuthProvider(AuthProvider.GOOGLE);
        return userRepository.save(newUser);
    }

    public UserEntity getUserFromToken(String token) {
        String email = jwtService.extractEmail(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
