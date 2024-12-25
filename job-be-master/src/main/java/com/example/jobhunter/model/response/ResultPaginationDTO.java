package com.example.jobhunter.model.response;

import lombok.Data;

@Data
public class ResultPaginationDTO {
  private Meta meta;
  private Object result;

  @Data
  public static class Meta {
    private int page;
    private int pageSize;
    private int pages;
    private long total;
  }
}
