package edu.sfwe405.campusmarketplace.service;

import edu.sfwe405.campusmarketplace.dto.ForgotPasswordRequest;
import edu.sfwe405.campusmarketplace.dto.ForgotPasswordResponse;
import edu.sfwe405.campusmarketplace.dto.RegisterRequest;
import edu.sfwe405.campusmarketplace.dto.RegisterResponse;
import edu.sfwe405.campusmarketplace.dto.ResetPasswordRequest;
import edu.sfwe405.campusmarketplace.dto.ResetPasswordResponse;
import edu.sfwe405.campusmarketplace.dto.UpdateUserRequest;
import edu.sfwe405.campusmarketplace.model.UserAccount;
import edu.sfwe405.campusmarketplace.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public RegisterResponse createUser(RegisterRequest request) {
        String cleanEmail = request.email().trim().toLowerCase();

        if (userRepository.findByEmail(cleanEmail).isPresent()) {
            throw new IllegalArgumentException("Email is already registered");
        }

        UserAccount user = new UserAccount();
        user.setEmail(cleanEmail);

        String hashed = passwordEncoder.encode(request.password());
        user.setPassword(hashed);

        userRepository.save(user);

        return new RegisterResponse(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }

    public List<RegisterResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> new RegisterResponse(
                        user.getId(),
                        user.getEmail(),
                        user.getRole(),
                        user.getCreatedAt()
                ))
                .toList();
    }

    public UserAccount getByEmailOrThrow(String email) {
        String cleanEmail = email.trim().toLowerCase();

        return userRepository.findByEmail(cleanEmail)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
    }

    public void updatePassword(String email, String newPassword) {
        UserAccount user = getByEmailOrThrow(email);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public boolean matchesPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public void deleteAccount(String email) {
        UserAccount user = getByEmailOrThrow(email);
        userRepository.delete(user);
    }

    public String updateAccount(String currentEmail, UpdateUserRequest request) {
        UserAccount user = getByEmailOrThrow(currentEmail);

        if (request.email() != null && !request.email().isBlank()) {
            String cleanEmail = request.email().trim().toLowerCase();

            if (!cleanEmail.equals(user.getEmail())
                    && userRepository.findByEmail(cleanEmail).isPresent()) {
                throw new IllegalArgumentException("Email is already registered.");
            }

            user.setEmail(cleanEmail);
        }

        if (request.password() != null && !request.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }

        userRepository.save(user);

        return user.getEmail();
    }

    private final Map<String, String> tokenStore = new ConcurrentHashMap<>();

    public ForgotPasswordResponse requestReset(ForgotPasswordRequest request) {
        String cleanEmail = request.email().trim().toLowerCase();

        getByEmailOrThrow(cleanEmail);

        String token = UUID.randomUUID().toString();
        tokenStore.put(token, cleanEmail);

        return new ForgotPasswordResponse("Password reset token generated.", token);
    }

    public ResetPasswordResponse resetPassword(ResetPasswordRequest request) {
        String email = tokenStore.get(request.resetToken());

        if (email == null) {
            throw new IllegalArgumentException("Invalid or expired reset token.");
        }

        updatePassword(email, request.newPassword());
        tokenStore.remove(request.resetToken());

        return new ResetPasswordResponse("Password has been reset successfully.");
    }
}