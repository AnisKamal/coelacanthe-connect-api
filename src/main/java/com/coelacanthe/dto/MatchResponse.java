package com.coelacanthe.dto;

import java.time.LocalDateTime;

public record MatchResponse(Long id,
                            String homeTeam,
                            String awayTeam,
                            LocalDateTime matchDate,
                            String competition,
                            String lieu,
                            Boolean preMatchNotificationSent,
                            Boolean postMatchNotificationSent
                            ) {
}
