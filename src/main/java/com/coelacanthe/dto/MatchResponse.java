package com.coelacanthe.dto;

import java.time.Instant;
import java.time.LocalDateTime;

public record MatchResponse(Long id,
                            String homeTeam,
                            String awayTeam,
                            Instant matchDate,
                            String competition,
                            String lieu,
                            Boolean preMatchNotificationSent,
                            Boolean postMatchNotificationSent
                            ) {
}
