package com.coelacanthe.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Configuration
public class FireBaseConfig {

    @PostConstruct
    public void initialize() throws IOException {
        if(FirebaseApp.getApps().isEmpty()) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(
                            new ClassPathResource("coelacanthe-connect-firebase-adminsdk-fbsvc-3b7498f78d.json").getInputStream()
                    ))
                    .build();
            FirebaseApp.initializeApp(options);
        }
    }

}
