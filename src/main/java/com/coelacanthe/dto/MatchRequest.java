package com.coelacanthe.dto;

import java.time.Instant;
import java.time.LocalDateTime;

public record MatchRequest(String homeTeam,
                           String awayTeam,
                           Instant matchDate,
                           String competition,
                           String lieu ,
                           String externalId) {
}
