package com.example.urlshortener.api.dto;

import java.time.Instant;

public class StatsResponse {
  private String originalUrl;
  private Long clickCount;
  private Instant createdAt;

  public StatsResponse() {}

  public StatsResponse(String originalUrl, Long clickCount, Instant createdAt) {
    this.originalUrl = originalUrl;
    this.clickCount = clickCount;
    this.createdAt = createdAt;
  }

  public String getOriginalUrl() {
    return originalUrl;
  }

  public void setOriginalUrl(String originalUrl) {
    this.originalUrl = originalUrl;
  }

  public Long getClickCount() {
    return clickCount;
  }

  public void setClickCount(Long clickCount) {
    this.clickCount = clickCount;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }
}

