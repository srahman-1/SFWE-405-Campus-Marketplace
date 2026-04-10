package edu.sfwe405.campusmarketplace.dto;

import java.util.List;

public record CartResponse(
    Long buyerId,
    List<CartItemResponse> items,
    double total
) {}
