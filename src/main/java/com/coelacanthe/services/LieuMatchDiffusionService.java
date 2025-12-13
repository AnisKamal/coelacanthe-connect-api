package com.coelacanthe.services;

import com.coelacanthe.dto.LieuMatchDiffusionRequest;
import com.coelacanthe.dto.LieuMatchDiffusionResponse;
import com.coelacanthe.entities.LieuDiffusionMatchEntity;
import com.coelacanthe.entities.MatchEntity;
import com.coelacanthe.exception.MatchNotFoundException;
import com.coelacanthe.repositories.LieuMatchDiffusionRepository;
import com.coelacanthe.repositories.MatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LieuMatchDiffusionService {

    private final LieuMatchDiffusionRepository  lieuMatchDiffusionRepository;

    private final MatchRepository matchRepository;

    public List<LieuMatchDiffusionResponse> findAllByMatch_Id(Long matchId) {

        List<LieuDiffusionMatchEntity> list = lieuMatchDiffusionRepository.findAllByMatch_Id(matchId);

        List<LieuMatchDiffusionResponse> lieuMatchDiffusionResponseList = new ArrayList<>();

        for(LieuDiffusionMatchEntity lieuDiffusionMatchEntity : list) {
            lieuMatchDiffusionResponseList.add(new  LieuMatchDiffusionResponse(
                    lieuDiffusionMatchEntity.getId(),
                    lieuDiffusionMatchEntity.getPays(),
                    lieuDiffusionMatchEntity.getVille(),
                    lieuDiffusionMatchEntity.getAddresse()));
        }
        return lieuMatchDiffusionResponseList;
    }

    public LieuMatchDiffusionResponse saveLieuMatchDiffusion(LieuMatchDiffusionRequest lieuMatchDiffusionRequest) {
        MatchEntity matchEntity = matchRepository.findById(lieuMatchDiffusionRequest.idMatch())
                .orElseThrow(() -> new MatchNotFoundException(
                        "Match non trouvé avec l'id: " + lieuMatchDiffusionRequest.idMatch()
                ));
            log.info("Match found with id: " + lieuMatchDiffusionRequest.idMatch());
            LieuDiffusionMatchEntity lieuDiffusionMatchEntity = new LieuDiffusionMatchEntity();
            lieuDiffusionMatchEntity.setMatch(matchEntity);
            lieuDiffusionMatchEntity.setPays(lieuMatchDiffusionRequest.pays());
            lieuDiffusionMatchEntity.setVille(lieuMatchDiffusionRequest.ville());
            lieuDiffusionMatchEntity.setAddresse(lieuMatchDiffusionRequest.addresse());

             lieuDiffusionMatchEntity =  lieuMatchDiffusionRepository.save(lieuDiffusionMatchEntity);

             log.info("LieuMatchDiffusion save successful");

             LieuMatchDiffusionResponse lieuMatchDiffusionResponse = new LieuMatchDiffusionResponse(
                     lieuDiffusionMatchEntity.getId(),
                     lieuDiffusionMatchEntity.getPays(),
                     lieuDiffusionMatchEntity.getVille(),
                     lieuDiffusionMatchEntity.getAddresse()
             );
             return lieuMatchDiffusionResponse;
    }




}
