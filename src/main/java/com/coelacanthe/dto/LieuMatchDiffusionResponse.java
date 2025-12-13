package com.coelacanthe.dto;

import java.time.LocalDateTime;

public record LieuMatchDiffusionResponse(
        Long id,
        String pays,
        String ville,
        String addresse
) {
}
