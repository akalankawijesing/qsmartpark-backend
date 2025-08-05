package com.smart.q.smartq.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smart.q.smartq.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    // custom queries if needed
}
