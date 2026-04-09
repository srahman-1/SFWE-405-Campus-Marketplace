package edu.sfwe405.campusmarketplace.controller;

import edu.sfwe405.campusmarketplace.dto.RegisterRequest;
import edu.sfwe405.campusmarketplace.dto.RegisterResponse;
import edu.sfwe405.campusmarketplace.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
