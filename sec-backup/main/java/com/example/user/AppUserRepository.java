package com.example.user;

import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;



public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    
    Optional<AppUser> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}
