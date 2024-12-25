package com.example.jobhunter.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.jobhunter.model.User;

public interface UserRepo extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
  User findByEmail(String email);

  User findByRefreshTokenAndEmail(String refreshToken, String email);
}
