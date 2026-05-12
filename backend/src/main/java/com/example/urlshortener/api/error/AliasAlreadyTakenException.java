package com.example.urlshortener.api.error;

public class AliasAlreadyTakenException extends RuntimeException {
  public AliasAlreadyTakenException() {
    super("Alias already taken");
  }
}

