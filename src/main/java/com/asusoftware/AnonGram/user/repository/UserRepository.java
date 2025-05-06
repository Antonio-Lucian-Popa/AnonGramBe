package com.asusoftware.AnonGram.user.repository;


import com.asusoftware.AnonGram.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    User findByKeycloakId(UUID keycloakId);
}
