package com.example.urlshortener.redirect;

import com.example.urlshortener.api.error.NotFoundException;
import com.example.urlshortener.url.UrlService;
import java.net.URI;
import java.util.Optional;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedirectController {
  private final UrlService service;

  public RedirectController(UrlService service) {
    this.service = service;
  }

  @GetMapping("/{shortCode}")
  public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
    Optional<String> originalOpt = service.resolveOriginalUrl(shortCode);
    if (originalOpt.isEmpty()) {
      throw new NotFoundException("Short code not found");
    }

    service.incrementClickCount(shortCode);
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(URI.create(originalOpt.get()));
    return new ResponseEntity<>(headers, HttpStatus.FOUND);
  }
}

