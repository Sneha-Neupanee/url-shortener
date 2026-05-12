package com.example.urlshortener.url;

import com.example.urlshortener.api.dto.ShortenUrlRequest;
import com.example.urlshortener.api.dto.ShortenUrlResponse;
import com.example.urlshortener.api.dto.StatsResponse;
import com.example.urlshortener.api.error.AliasAlreadyTakenException;
import com.example.urlshortener.api.error.NotFoundException;
import java.time.Duration;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UrlService {
  private final UrlMappingRepository repository;
  private final ShortCodeGenerator generator;
  private final StringRedisTemplate redis;
  private final String baseUrl;
  private final Duration redisTtl;

  public UrlService(
      UrlMappingRepository repository,
      ShortCodeGenerator generator,
      StringRedisTemplate redis,
      @Value("${app.base-url}") String baseUrl,
      @Value("${app.redis.ttl-seconds:86400}") long redisTtlSeconds) {
    this.repository = repository;
    this.generator = generator;
    this.redis = redis;
    this.baseUrl = baseUrl;
    this.redisTtl = Duration.ofSeconds(redisTtlSeconds);
  }

  @Transactional
  public ShortenUrlResponse shorten(ShortenUrlRequest req) {
    String shortCode;

    if (req.getCustomAlias() != null && !req.getCustomAlias().isBlank()) {
      shortCode = req.getCustomAlias().trim();
      if (repository.existsByShortCode(shortCode)) {
        throw new AliasAlreadyTakenException();
      }
    } else {
      shortCode = generateUniqueShortCode();
    }

    UrlMapping m = new UrlMapping();
    m.setShortCode(shortCode);
    m.setOriginalUrl(req.getLongUrl().trim());
    repository.save(m);

    cache(shortCode, m.getOriginalUrl());
    return new ShortenUrlResponse(baseUrl + "/" + shortCode);
  }

  public StatsResponse stats(String shortCode) {
    UrlMapping m =
        repository
            .findByShortCode(shortCode)
            .orElseThrow(() -> new NotFoundException("Short code not found"));
    return new StatsResponse(m.getOriginalUrl(), m.getClickCount(), m.getCreatedAt());
  }

  /**
   * Resolve original URL for redirect, using Redis as read-through cache.
   * Returns empty if the short code does not exist.
   */
  public Optional<String> resolveOriginalUrl(String shortCode) {
    String cached = redis.opsForValue().get(shortCode);
    if (cached != null && !cached.isBlank()) {
      return Optional.of(cached);
    }

    Optional<UrlMapping> fromDb = repository.findByShortCode(shortCode);
    fromDb.ifPresent(m -> cache(shortCode, m.getOriginalUrl()));
    return fromDb.map(UrlMapping::getOriginalUrl);
  }

  public void incrementClickCount(String shortCode) {
    repository.incrementClickCount(shortCode);
  }

  private void cache(String shortCode, String originalUrl) {
    if (originalUrl == null || originalUrl.isBlank()) return;
    redis.opsForValue().set(shortCode, originalUrl, redisTtl);
  }

  private String generateUniqueShortCode() {
    int attempts = 0;
    while (attempts < 20) {
      String code = generator.randomCode();
      if (!repository.existsByShortCode(code)) {
        return code;
      }
      attempts++;
    }
    throw new IllegalStateException("Could not generate unique short code");
  }
}

