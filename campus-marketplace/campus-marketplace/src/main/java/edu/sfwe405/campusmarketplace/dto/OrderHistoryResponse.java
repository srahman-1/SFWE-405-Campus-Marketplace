package edu.sfwe405.campusmarketplace.dto;

import java.time.LocalDateTime;

public record OrderHistoryResponse(
    Long orderId,
    String transactionType,
    Long buyerId,
    String buyerEmail,
    Long productId,
    String productName,
    double productPrice,
    int quantity,
    boolean paid,
    LocalDateTime createdAt
) {}
