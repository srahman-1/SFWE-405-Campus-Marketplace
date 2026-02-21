package edu.sfwe405.campusmarketplace.dto;

import java.time.LocalDateTime;

import edu.sfwe405.campusmarketplace.model.UserAccount.Role;

// This is a temp name
public record RegisterResponse(
    Long id,
    String email,
    Role role,
    LocalDateTime createdAt
) {}