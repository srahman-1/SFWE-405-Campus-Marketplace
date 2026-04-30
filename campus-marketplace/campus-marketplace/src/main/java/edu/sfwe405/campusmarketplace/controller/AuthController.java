package edu.sfwe405.campusmarketplace.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.sfwe405.campusmarketplace.dto.ForgotPasswordRequest;
import edu.sfwe405.campusmarketplace.dto.ForgotPasswordResponse;
import edu.sfwe405.campusmarketplace.dto.LoginRequest;
import edu.sfwe405.campusmarketplace.dto.LoginResponse;
import edu.sfwe405.campusmarketplace.dto.RegisterRequest;
import edu.sfwe405.campusmarketplace.dto.RegisterResponse;
import edu.sfwe405.campusmarketplace.dto.ResetPasswordRequest;
import edu.sfwe405.campusmarketplace.dto.ResetPasswordResponse;
import edu.sfwe405.campusmarketplace.service.AuthService;
import edu.sfwe405.campusmarketplace.service.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService,
            UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public RegisterResponse register(@Valid @RequestBody RegisterRequest request) {
        return userService.createUser(request);
    }

    /*@PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }*/
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException error) {
            return ResponseEntity.status(401).body(Map.of(
                    "message", error.getMessage()
            ));
        } catch (Exception error) {
            error.printStackTrace();

            return ResponseEntity.internalServerError().body(Map.of(
                    "message", error.getClass().getSimpleName() + ": " + error.getMessage()
            ));
        }
    }

    // ── password reminder endpoints ───────────────────────────────────────
    /**
     * Step 1 – request a password reset token.
     *
     * POST /auth/forgot-password Body: { "email": "user@example.com" }
     *
     * Returns a mock reset token (in production this would be emailed).
     */
    @PostMapping("/forgot-password")
    public ForgotPasswordResponse forgotPassword(@RequestBody ForgotPasswordRequest request) {
        return userService.requestReset(request);
    }

    /**
     * Step 2 – reset the password using the token from step 1.
     *
     * POST /auth/reset-password Body: { "resetToken": "<token>", "newPassword":
     * "newpass123" }
     */
    @PostMapping("/reset-password")
    public ResetPasswordResponse resetPassword(@RequestBody ResetPasswordRequest request) {
        return userService.resetPassword(request);
    }
}
