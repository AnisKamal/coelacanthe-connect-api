package com.coelacanthe.dto;

import java.time.LocalDateTime;

public record FcmTokenResponse(Long Id,
                               String token,
                               String deviceInfo,
                               Boolean isActive,
                               LocalDateTime createdAt,
                               LocalDateTime updatedAt) {
}
