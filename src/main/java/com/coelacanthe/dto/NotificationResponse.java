package com.coelacanthe.dto;

public record NotificationResponse(int successCount,

                                   int failureCount,

                                   String message) {
}
