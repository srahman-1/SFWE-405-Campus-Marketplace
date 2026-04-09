package edu.sfwe405.campusmarketplace.dto;

public record ForgotPasswordResponse(
        String message,
        String resetToken   // mock token — in production this would be emailed
) {}