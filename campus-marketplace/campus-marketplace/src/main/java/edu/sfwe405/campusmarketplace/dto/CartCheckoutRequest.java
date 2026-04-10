package edu.sfwe405.campusmarketplace.dto;

import jakarta.validation.constraints.NotBlank;

public record CartCheckoutRequest(
    @NotBlank
    String paymentMethod,
    boolean forcePaymentFailure
) {}
