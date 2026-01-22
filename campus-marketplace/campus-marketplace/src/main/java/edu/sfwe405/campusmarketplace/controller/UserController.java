package edu.sfwe405.campusmarketplace.controller;

import edu.sfwe405.campusmarketplace.model.UserAccount;
import edu.sfwe405.campusmarketplace.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @PostMapping
    public UserAccount createUser(@RequestBody UserAccount user) {
        return userRepository.save(user);
    }

    // GET all users
    @GetMapping
    public List<UserAccount> getAllUsers() {
        return userRepository.findAll();
    }
}
