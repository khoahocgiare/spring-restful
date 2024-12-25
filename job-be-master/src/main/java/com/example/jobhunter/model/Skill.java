package com.example.jobhunter.model;

import java.time.Instant;
import java.util.List;

import com.example.jobhunter.util.SecurityUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "skills")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Skill {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String name;

  private Instant createdAt;
  private Instant updatedAt;
  private String createdBy;
  private String updatedBy;

  @ManyToMany(fetch = FetchType.LAZY, mappedBy = "skills")
  @JsonIgnore
  private List<Job> jobs;

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
