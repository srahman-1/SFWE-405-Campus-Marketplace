package edu.sfwe405.campusmarketplace.service;

import edu.sfwe405.campusmarketplace.dto.ForgotPasswordRequest;
import edu.sfwe405.campusmarketplace.dto.ForgotPasswordResponse;
import edu.sfwe405.campusmarketplace.dto.ResetPasswordRequest;
import edu.sfwe405.campusmarketplace.dto.ResetPasswordResponse;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class PasswordResetService {

    // token -> email
    private final Map<String, String> tokenStore = new ConcurrentHashMap<>();

    private final UserService userService;

    public PasswordResetService(UserService userService) {
        this.userService = userService;
    }

    public ForgotPasswordResponse requestReset(ForgotPasswordRequest request) {
        String cleanEmail = request.email().trim().toLowerCase();

        userService.getByEmailOrThrow(cleanEmail);

        String token = UUID.randomUUID().toString();
        tokenStore.put(token, cleanEmail);

        return new ForgotPasswordResponse(
                "Password reset token generated.",
                token
        );
    }


    public ResetPasswordResponse resetPassword(ResetPasswordRequest request) {
        String email = tokenStore.get(request.resetToken());

        if (email == null) {
            throw new IllegalArgumentException("Invalid or expired reset token.");
        }

        userService.updatePassword(email, request.newPassword());

        // Invalidate token after single use
        tokenStore.remove(request.resetToken());

        return new ResetPasswordResponse("Password has been reset successfully.");
    }
}