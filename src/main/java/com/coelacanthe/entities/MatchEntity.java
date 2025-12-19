package com.coelacanthe.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "t_match")
@Getter
@Setter
@NoArgsConstructor
public class MatchEntity extends BaseEntity {

    @Column(name = "home_team",nullable = false)
    private String homeTeam;

    @Column(name = "away_team",nullable = false)
    private String awayTeam;

    @Column(name = "match_date", nullable = false)
    private Instant matchDate;

    private String competition;

    private String lieu;


    private Boolean preMatchNotificationSent = false;


    private Boolean postMatchNotificationSent = false;


    private String externalMatchId;

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL)
    private List<LieuDiffusionMatchEntity> lieuDiffusionMatches;
}
