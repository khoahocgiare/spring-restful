package com.example.jobhunter.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

public class ResLoginDTO {
  @JsonProperty("access_token")
  private String accessToken;
  private UserInfo user;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class UserInfo {
    private long id;
    private String email;
    private String name;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class UserGetAccount {
    private UserInfo user;
  }
}
