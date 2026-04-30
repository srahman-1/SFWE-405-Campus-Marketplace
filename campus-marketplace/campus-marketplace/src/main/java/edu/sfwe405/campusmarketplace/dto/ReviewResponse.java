package edu.sfwe405.campusmarketplace.dto;

import java.time.LocalDateTime;

public record ReviewResponse(
        Long id,
        Long productId,
        Long orderId,
        String reviewerEmail,
        int rating,
        String comment,
        LocalDateTime createdAt
) {
}