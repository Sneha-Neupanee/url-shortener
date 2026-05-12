package com.example.urlshortener.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public class ShortenUrlRequest {
  @NotBlank(message = "longUrl is required")
  @URL(message = "longUrl must be a valid URL")
  @Size(max = 2048, message = "longUrl is too long")
  private String longUrl;

  @Size(min = 3, max = 30, message = "customAlias length must be 3-30")
  @Pattern(
      regexp = "^[a-zA-Z0-9_-]+$",
      message = "customAlias can contain only a-z A-Z 0-9 _ -"
  )
  private String customAlias;

  public String getLongUrl() {
    return longUrl;
  }

  public void setLongUrl(String longUrl) {
    this.longUrl = longUrl;
  }

  public String getCustomAlias() {
    return customAlias;
  }

  public void setCustomAlias(String customAlias) {
    this.customAlias = customAlias;
  }
}

