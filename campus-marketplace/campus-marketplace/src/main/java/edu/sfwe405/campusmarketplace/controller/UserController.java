package edu.sfwe405.campusmarketplace.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.sfwe405.campusmarketplace.dto.RegisterRequest;
import edu.sfwe405.campusmarketplace.dto.RegisterResponse;
import edu.sfwe405.campusmarketplace.dto.UpdateUserRequest;
import edu.sfwe405.campusmarketplace.service.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public RegisterResponse createUser(@Valid @RequestBody RegisterRequest user) {
        return userService.createUser(user);
    }

    @GetMapping
    public List<RegisterResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Map<String, String>> deleteAccount(Authentication authentication) {
        String email = authentication.getName();
        userService.deleteAccount(email);
        return ResponseEntity.ok(Map.of(
                "message", "Account for " + email + " has been successfully deleted."
        ));
    }

    @PutMapping("/me")
    public ResponseEntity<Map<String, String>> updateAccount(
            Authentication authentication,
            @RequestBody UpdateUserRequest request) {

        try {
            String currentEmail = authentication.getName();
            String updatedEmail = userService.updateAccount(currentEmail, request);

            return ResponseEntity.ok(Map.of(
                    "message", "Account updated successfully.",
                    "email", updatedEmail
            ));
        } catch (IllegalArgumentException error) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", error.getMessage()
            ));
        } catch (Exception error) {
            error.printStackTrace();

            return ResponseEntity.internalServerError().body(Map.of(
                    "message", error.getClass().getSimpleName() + ": " + error.getMessage()
            ));
        }
    }
}
