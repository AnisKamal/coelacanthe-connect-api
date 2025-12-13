package com.coelacanthe.entities;

import com.coelacanthe.entities.enums.AuthProvider;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "t_user")
@Getter
@Setter
@NoArgsConstructor
public class UserEntity extends BaseEntity {

    @Column(nullable = false)
    private String email ;

    @Column(nullable = false)
    private String name;

    private String pictureUrl;

    private String googleId ;

    private AuthProvider authProvider;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<FcmTokenEntity> fcmTokens = new ArrayList<>();

}
