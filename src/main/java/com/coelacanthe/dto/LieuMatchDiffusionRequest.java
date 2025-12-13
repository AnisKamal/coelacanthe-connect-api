package com.coelacanthe.dto;

public record LieuMatchDiffusionRequest(Long idMatch,
                                        String pays,
                                        String ville,
                                        String addresse) {
}
