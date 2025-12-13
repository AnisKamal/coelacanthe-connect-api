package com.coelacanthe.controller;

import com.coelacanthe.dto.MatchRequest;
import com.coelacanthe.dto.MatchResponse;
import com.coelacanthe.services.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/match")
public class MatchController {

    private final MatchService matchService;

    @PostMapping
    public ResponseEntity<MatchResponse> saveMatch(@RequestBody MatchRequest matchRequest) {
        MatchResponse matchResponse = matchService.registerMatch(matchRequest);
        return ResponseEntity.ok().body(matchResponse);
    }
}
