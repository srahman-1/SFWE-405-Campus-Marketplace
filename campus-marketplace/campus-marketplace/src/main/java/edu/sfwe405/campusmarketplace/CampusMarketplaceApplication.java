package edu.sfwe405.campusmarketplace;

import edu.sfwe405.campusmarketplace.model.Product;
import edu.sfwe405.campusmarketplace.model.UserAccount;
import edu.sfwe405.campusmarketplace.repository.ProductRepository;
import edu.sfwe405.campusmarketplace.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class CampusMarketplaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusMarketplaceApplication.class, args);
    }

    @Bean
    CommandLineRunner seedData(
        UserRepository userRepository,
        ProductRepository productRepository,
        PasswordEncoder passwordEncoder
    ) {
        return args -> {
            if (userRepository.findByEmail("buyer@arizona.edu").isEmpty()) {
                UserAccount buyer = new UserAccount();
                buyer.setEmail("buyer@arizona.edu");
                buyer.setPassword(passwordEncoder.encode("Password1!"));
                userRepository.save(buyer);
            }

            if (userRepository.findByEmail("seller@arizona.edu").isEmpty()) {
                UserAccount seller = new UserAccount();
                seller.setEmail("seller@arizona.edu");
                seller.setPassword(passwordEncoder.encode("Password1!"));
                userRepository.save(seller);
            }

            if (productRepository.count() == 0) {
                Product product = new Product();

                product.setName("Textbook");
                product.setDescription("Textbook for Eller business intro");
                product.setPrice(25.00);
                product.setStock(2);
                productRepository.save(product);

                product = new Product();
                product.setName("Old Water Bottle");
                product.setDescription("Old water bottle with slight dent from dropping");
                product.setPrice(12.50);
                product.setStock(1);
                productRepository.save(product);

                product = new Product();
                product.setName("Set of writing tools");
                product.setDescription("Some mechanical pencils and pens");
                product.setPrice(45.00);
                product.setStock(1);
                productRepository.save(product);

                product = new Product();
                product.setName("Old macbook");
                product.setDescription("An old macbook.");
                product.setPrice(250);
                product.setStock(1);
                productRepository.save(product);

                product = new Product();
                product.setName("Bags of chips");
                product.setDescription("Bags of chips from costco.");
                product.setPrice(1);
                product.setStock(15);
                productRepository.save(product);

                product = new Product();
                product.setName("Mini Fridge");
                product.setDescription("Mini fridge that can fit in a dorm.");
                product.setPrice(90.00);
                product.setStock(1);
                productRepository.save(product);
            }
        };
    }
}
