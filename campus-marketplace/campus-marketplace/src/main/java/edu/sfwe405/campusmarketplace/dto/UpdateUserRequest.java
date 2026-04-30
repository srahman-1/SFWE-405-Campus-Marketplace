package edu.sfwe405.campusmarketplace.dto;

public record UpdateUserRequest(
        String email,
        String password
) {
}