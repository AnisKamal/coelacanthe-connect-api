package com.coelacanthe.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "t_lieu_diffusion_match")
public class LieuDiffusionMatchEntity extends BaseEntity{

    private String pays ;

    private String ville ;

    private String addresse ;

    @ManyToOne
    @JoinColumn(name="match_id")
    private MatchEntity match;
}
