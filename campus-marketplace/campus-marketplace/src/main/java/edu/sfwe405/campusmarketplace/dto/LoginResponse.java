package edu.sfwe405.campusmarketplace.dto;

import edu.sfwe405.campusmarketplace.model.UserAccount.Role;

public record LoginResponse(
    String token,
    String tokenType,
    long expiresInSeconds,
    String email,
    Role role,
    Long id
) {}
