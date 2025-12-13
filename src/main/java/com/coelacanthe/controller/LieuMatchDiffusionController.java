package com.coelacanthe.controller;

import com.coelacanthe.entities.LieuDiffusionMatchEntity;
import com.coelacanthe.services.LieuMatchDiffusionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/lieu-diffusion")
@Slf4j
public class LieuMatchDiffusionController {

    private final LieuMatchDiffusionService lieuMatchDiffusionService;


}
