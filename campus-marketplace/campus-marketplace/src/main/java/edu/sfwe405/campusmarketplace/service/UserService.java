package edu.sfwe405.campusmarketplace.service;

import edu.sfwe405.campusmarketplace.model.UserAccount;
import edu.sfwe405.campusmarketplace.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserAccount createUser(UserAccount user) {
        return userRepository.save(user);
    }

    public List<UserAccount> getAllUsers() {
        return userRepository.findAll();
    }
}
