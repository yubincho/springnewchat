package com.example.banktest.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // save 이미 만들어져 있음


    Optional<User> findByUsername(String username);  // jpa named query 작동




}
