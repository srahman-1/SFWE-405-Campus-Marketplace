package edu.sfwe405.campusmarketplace.controller;

import edu.sfwe405.campusmarketplace.dto.ForgotPasswordRequest;
import edu.sfwe405.campusmarketplace.dto.ForgotPasswordResponse;
import edu.sfwe405.campusmarketplace.dto.LoginRequest;
import edu.sfwe405.campusmarketplace.dto.LoginResponse;
import edu.sfwe405.campusmarketplace.dto.RegisterRequest;
import edu.sfwe405.campusmarketplace.dto.RegisterResponse;
import edu.sfwe405.campusmarketplace.dto.ResetPasswordRequest;
import edu.sfwe405.campusmarketplace.dto.ResetPasswordResponse;
import edu.sfwe405.campusmarketplace.service.AuthService;
import edu.sfwe405.campusmarketplace.service.PasswordResetService;
import edu.sfwe405.campusmarketplace.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final PasswordResetService passwordResetService;

    public AuthController(AuthService authService,
                          UserService userService,
                          PasswordResetService passwordResetService) {
        this.authService = authService;
        this.userService = userService;
        this.passwordResetService = passwordResetService;
    }

    // ── existing endpoints (unchanged) ────────────────────────────────────

    @PostMapping("/register")
    public RegisterResponse register(@RequestBody RegisterRequest request) {
        return userService.createUser(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    // ── password reminder endpoints ───────────────────────────────────────

    /**
     * Step 1 – request a password reset token.
     *
     * POST /auth/forgot-password
     * Body: { "email": "user@example.com" }
     *
     * Returns a mock reset token (in production this would be emailed).
     */
    @PostMapping("/forgot-password")
    public ForgotPasswordResponse forgotPassword(@RequestBody ForgotPasswordRequest request) {
        return passwordResetService.requestReset(request);
    }

    /**
     * Step 2 – reset the password using the token from step 1.
     *
     * POST /auth/reset-password
     * Body: { "resetToken": "<token>", "newPassword": "newpass123" }
     */
    @PostMapping("/reset-password")
    public ResetPasswordResponse resetPassword(@RequestBody ResetPasswordRequest request) {
        return passwordResetService.resetPassword(request);
    }
}