package edu.sfwe405.campusmarketplace.dto;

import java.util.List;

public record CartCheckoutResponse(
    boolean success,
    String message,
    double total,
    List<Long> orderIds,
    List<Long> unavailableItems
) {}
