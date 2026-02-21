package edu.sfwe405.campusmarketplace.dto;

import edu.sfwe405.campusmarketplace.model.UserAccount.Role;

public record RegisterRequest(
    String email,
    String password,
    Role role
) {}