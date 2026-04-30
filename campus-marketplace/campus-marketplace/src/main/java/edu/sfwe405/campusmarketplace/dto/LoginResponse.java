package edu.sfwe405.campusmarketplace.dto;

public record LoginResponse(
    String token,
    long expiresInSeconds,
    String email,
    Long id
) {}
