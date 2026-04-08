package edu.sfwe405.campusmarketplace.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductDTO(
    @NotBlank
    @Size(max = 120)
    String name,
    @Size(max = 500)
    String description,
    @NotNull
    @DecimalMin("0.01")
    Double price
) {}
