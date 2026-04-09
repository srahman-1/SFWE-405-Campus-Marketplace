package edu.sfwe405.campusmarketplace.controller;

import edu.sfwe405.campusmarketplace.dto.RegisterRequest;
import edu.sfwe405.campusmarketplace.dto.RegisterResponse;
import edu.sfwe405.campusmarketplace.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public RegisterResponse createUser(@RequestBody RegisterRequest user) {
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
}
