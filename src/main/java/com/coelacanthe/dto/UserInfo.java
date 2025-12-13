package com.coelacanthe.dto;

public record UserInfo(Long id,
                       String email,
                       String name,
                       String photoUrl) {
}
