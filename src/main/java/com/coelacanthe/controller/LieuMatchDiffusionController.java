package com.coelacanthe.controller;

import com.coelacanthe.dto.LieuMatchDiffusionRequest;
import com.coelacanthe.dto.LieuMatchDiffusionResponse;
import com.coelacanthe.entities.LieuDiffusionMatchEntity;
import com.coelacanthe.repositories.LieuMatchDiffusionRepository;
import com.coelacanthe.services.LieuMatchDiffusionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/lieu-diffusion")
@Slf4j
public class LieuMatchDiffusionController {

    private final LieuMatchDiffusionService lieuMatchDiffusionService;


    @GetMapping
    public ResponseEntity<List<LieuMatchDiffusionResponse>> getListLieuMatchDiffusion(@RequestParam Long matchId){
        List<LieuMatchDiffusionResponse> response =  lieuMatchDiffusionService.findAllByMatch_Id(matchId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LieuMatchDiffusionResponse> saveMatchDiffusion(@RequestBody LieuMatchDiffusionRequest lieuMatchDiffusionRequest){
        LieuMatchDiffusionResponse response = lieuMatchDiffusionService.saveLieuMatchDiffusion(lieuMatchDiffusionRequest) ;
        return ResponseEntity.ok(response);
    }



}
