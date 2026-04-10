package edu.sfwe405.campusmarketplace.service;

import edu.sfwe405.campusmarketplace.dto.LoginRequest;
import edu.sfwe405.campusmarketplace.dto.LoginResponse;
import edu.sfwe405.campusmarketplace.model.UserAccount;
import edu.sfwe405.campusmarketplace.security.JwtService;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthService(
        UserService userService,
        JwtService jwtService
    ) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        String cleanEmail = request.email().trim().toLowerCase();
        UserAccount user = userService.getByEmailOrThrow(cleanEmail);
        if (!userService.matchesPassword(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        String token = jwtService.generateToken(user);

        return new LoginResponse(
            token,
            jwtService.getTokenExpirationInSeconds(),
            user.getEmail(),
            user.getRole(),
            user.getId()
        );
    }

    public boolean userExists(String email) {
        try {
            userService.getByEmailOrThrow(email);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

}
