package com.coelacanthe.repositories;

import com.coelacanthe.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity>  findByGoogleId(String googleId);

    Optional<UserEntity>  findByEmail(String email);

}
