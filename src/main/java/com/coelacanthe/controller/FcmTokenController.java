package com.coelacanthe.controller;

import com.coelacanthe.dto.FcmTokenRequest;
import com.coelacanthe.dto.FcmTokenResponse;
import com.coelacanthe.services.FcmTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("api/v1/notification")
public class FcmTokenController {

    private final FcmTokenService fcmTokenService;

    @PostMapping
    private ResponseEntity<FcmTokenResponse> registerToken(@RequestBody FcmTokenRequest fcmTokenRequest){
        FcmTokenResponse fcmTokenResponse = fcmTokenService.registerToken(fcmTokenRequest);
        return ResponseEntity.ok(fcmTokenResponse);
    }

}
