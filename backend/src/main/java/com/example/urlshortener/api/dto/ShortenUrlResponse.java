package com.example.urlshortener.api.dto;

public class ShortenUrlResponse {
  private String shortUrl;

  public ShortenUrlResponse() {}

  public ShortenUrlResponse(String shortUrl) {
    this.shortUrl = shortUrl;
  }

  public String getShortUrl() {
    return shortUrl;
  }

  public void setShortUrl(String shortUrl) {
    this.shortUrl = shortUrl;
  }
}

