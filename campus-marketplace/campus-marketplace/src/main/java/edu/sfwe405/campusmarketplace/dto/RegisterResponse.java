package edu.sfwe405.campusmarketplace.dto;

import java.time.LocalDateTime;

public record RegisterResponse(
    Long id,
    String email,
    LocalDateTime createdAt
) {}
