package com.coelacanthe.repositories;

import com.coelacanthe.entities.LieuDiffusionMatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LieuMatchDiffusionRepository extends JpaRepository<LieuDiffusionMatchEntity, Long> {

    List<LieuDiffusionMatchEntity> findAllByMatch_Id(Long matchId);

    List<LieuDiffusionMatchEntity> findAllByPays(String pays);

}
