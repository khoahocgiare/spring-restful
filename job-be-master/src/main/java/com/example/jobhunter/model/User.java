package com.example.jobhunter.model;

import java.time.Instant;

import com.example.jobhunter.util.SecurityUtil;
import com.example.jobhunter.util.constant.GenderEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  @NotBlank(message = "Email is required")
  private String email;

  @NotBlank(message = "Password is required")
  private String password;

  private int age;

  @Enumerated(EnumType.STRING)
  private GenderEnum gender;

  private String address;

  @Column(columnDefinition = "MEDIUMTEXT")
  private String refreshToken;
  private Instant createdAt;
  private Instant updatedAt;
  private String createdBy;
  private String updatedBy;

  @ManyToOne
  @JoinColumn(name = "company_id")
  private Company company;

  @PrePersist
  public void handleBeforeCreate() {
    this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
        ? SecurityUtil.getCurrentUserLogin().get()
        : "";

    this.createdAt = Instant.now();
  }

  @PreUpdate
  public void handleBeforeUpdate() {
    this.updatedBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
        ? SecurityUtil.getCurrentUserLogin().get()
        : "";

    this.updatedAt = Instant.now();
  }
}
