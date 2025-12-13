package com.coelacanthe.dto;

import java.time.LocalDateTime;

public record MatchRequest(String homeTeam,
                           String awayTeam,
                           LocalDateTime matchDate,
                           String competition,
                           String lieu ,
                           String externalId) {
}
