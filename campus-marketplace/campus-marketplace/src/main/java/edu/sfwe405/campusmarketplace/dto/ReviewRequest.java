package edu.sfwe405.campusmarketplace.dto;

public record ReviewRequest(
        Long productId,
        Long orderId,
        Integer rating,
        String comment
) {
}