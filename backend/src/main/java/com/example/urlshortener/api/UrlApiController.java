package com.example.urlshortener.api;

import com.example.urlshortener.api.dto.ShortenUrlRequest;
import com.example.urlshortener.api.dto.ShortenUrlResponse;
import com.example.urlshortener.api.dto.StatsResponse;
import com.example.urlshortener.url.UrlService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UrlApiController {
  private final UrlService service;

  public UrlApiController(UrlService service) {
    this.service = service;
  }

  @PostMapping("/shorten")
  public ShortenUrlResponse shorten(@Valid @RequestBody ShortenUrlRequest req) {
    return service.shorten(req);
  }

  @GetMapping("/stats/{shortCode}")
  public StatsResponse stats(@PathVariable String shortCode) {
    return service.stats(shortCode);
  }
}

