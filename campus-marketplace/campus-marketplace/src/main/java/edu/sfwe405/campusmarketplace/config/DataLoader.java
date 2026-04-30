package edu.sfwe405.campusmarketplace.config;

import edu.sfwe405.campusmarketplace.model.UserAccount;
import edu.sfwe405.campusmarketplace.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Check if users already exist
            if (userRepository.count() > 0) {
                return; // Don't recreate if users already exist
            }

            // Create admin user
            UserAccount admin = new UserAccount();
            admin.setEmail("admin@test.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("admin");
            userRepository.save(admin);

            // Create regular user
            UserAccount user = new UserAccount();
            user.setEmail("user@test.com");
            user.setPassword(passwordEncoder.encode("password123"));
            user.setRole("user");
            userRepository.save(user);

            // Create second regular user
            UserAccount user2 = new UserAccount();
            user2.setEmail("student@test.com");
            user2.setPassword(passwordEncoder.encode("student123"));
            user2.setRole("user");
            userRepository.save(user2);

            System.out.println("Admin: admin@test.com / admin123");
            System.out.println("User1: user@test.com / password123");
            System.out.println("User2: student@test.com / student123");
        };
    }
}