package com.coelacanthe.controller;

import com.coelacanthe.dto.AuthResponse;
import com.coelacanthe.dto.GoogleTokenRequest;
import com.coelacanthe.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthentificationController {

    private final AuthService authService;

    @PostMapping("/google")
    public ResponseEntity<AuthResponse> authentification(@RequestBody GoogleTokenRequest request){
        AuthResponse response = authService.authenticateWithGoogle(request);
        return ResponseEntity.ok(response);
    }



}
