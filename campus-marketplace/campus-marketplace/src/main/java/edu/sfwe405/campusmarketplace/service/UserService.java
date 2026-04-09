package edu.sfwe405.campusmarketplace.service;

import edu.sfwe405.campusmarketplace.dto.RegisterRequest;
import edu.sfwe405.campusmarketplace.dto.RegisterResponse;
import edu.sfwe405.campusmarketplace.model.UserAccount;
import edu.sfwe405.campusmarketplace.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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
        user.setRole(request.role());

        String hashed = passwordEncoder.encode(request.password());
        user.setPassword(hashed);

        userRepository.save(user);
        return new RegisterResponse(user.getId(), user.getEmail(), user.getRole(), user.getCreatedAt());
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
}
