package com.coelacanthe.dto;

public record AuthResponse(String token,

                           String tokenType,

                           Long expiresIn,

                           UserInfo user
                    ) {
}
