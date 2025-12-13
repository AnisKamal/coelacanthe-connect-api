package com.coelacanthe.services;

import com.coelacanthe.dto.LieuMatchDiffusionRequest;
import com.coelacanthe.entities.LieuDiffusionMatchEntity;
import com.coelacanthe.repositories.LieuMatchDiffusionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LieuMatchDiffusionService {

    private final LieuMatchDiffusionRepository  lieuMatchDiffusionRepository;

    public List<LieuDiffusionMatchEntity> findAllByMatch_Id(Long matchId) {
        return lieuMatchDiffusionRepository.findAllByMatch_Id(matchId);
    }



}
