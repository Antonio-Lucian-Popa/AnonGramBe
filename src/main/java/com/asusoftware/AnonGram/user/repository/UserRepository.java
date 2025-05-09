package com.asusoftware.AnonGram.user.repository;


import com.asusoftware.AnonGram.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByKeycloakId(UUID keycloakId);
    @Query("SELECT u.alias FROM User u WHERE u.id = :id")
    Optional<String> findAliasById(@Param("id") UUID id);

}
