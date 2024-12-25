package com.example.jobhunter.model;

import java.time.Instant;

import com.example.jobhunter.util.SecurityUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "companies")
public class Company {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @NotBlank(message = "Tên công ty không được để trống")
  private String name;

  @Column(columnDefinition = "MEDIUMTEXT")
  private String description;

  private String address;

  private String logo;

  private Instant createdAt;

  private Instant updatedAt;

  private String createdBy;

  private String updatedBy;

  @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
  @JsonIgnore
  private List<User> users;

  @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
  @JsonIgnore
  private List<Job> jobs;

  @PrePersist
  public void onCreate() {
    this.createdBy = SecurityUtil.getCurrentUserLogin().orElse("");
    this.createdAt = Instant.now();
  }

  @PreUpdate
  public void onUpdate() {
    this.updatedBy = SecurityUtil.getCurrentUserLogin().orElse("");
    this.updatedAt = Instant.now();
  }
}
