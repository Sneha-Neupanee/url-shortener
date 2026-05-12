package com.example.urlshortener.url;

import java.security.SecureRandom;
import org.springframework.stereotype.Component;

@Component
public class ShortCodeGenerator {
  private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
  private static final int DEFAULT_LENGTH = 7;
  private final SecureRandom random = new SecureRandom();

  public String randomCode() {
    return randomCode(DEFAULT_LENGTH);
  }

  public String randomCode(int length) {
    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      int idx = random.nextInt(ALPHABET.length());
      sb.append(ALPHABET.charAt(idx));
    }
    return sb.toString();
  }
}

