package edu.sfwe405.campusmarketplace.service;

import edu.sfwe405.campusmarketplace.dto.RegisterRequest;
import edu.sfwe405.campusmarketplace.dto.RegisterResponse;
import edu.sfwe405.campusmarketplace.model.UserAccount;
import edu.sfwe405.campusmarketplace.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public RegisterResponse createUser(RegisterRequest request) {

        // Use a DTO so we don't need to send id or createdAt in the request
        // Could use access = JSONProperty stuff but defining that in the model mixes concerns
        UserAccount user = new UserAccount();
        user.setEmail(request.email());
        user.setRole(request.role());

        String hashed = passwordEncoder.encode(request.password());
        user.setPassword(hashed);

        userRepository.save(user);
        return new RegisterResponse(user.getId(), user.getEmail(), user.getRole(), user.getCreatedAt());
    }

    public List<RegisterResponse> getAllUsers() {
        // Use java stream
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
}
