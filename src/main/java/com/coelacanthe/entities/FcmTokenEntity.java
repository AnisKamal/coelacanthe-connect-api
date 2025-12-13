package com.coelacanthe.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "t_fcm_token",
        indexes = {
                @Index(name = "idx_token", columnList = "token")
        })
@Getter
@Setter
@NoArgsConstructor
public class FcmTokenEntity extends BaseEntity {

    @Column(nullable = false, unique = true, length = 500)
    private String token ;

    private String deviceInfo;

    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

}
