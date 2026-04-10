package edu.sfwe405.campusmarketplace.dto;

public record CartItemResponse(
    Long productId,
    String productName,
    int quantity,
    double unitPrice,
    double lineTotal
) {}
