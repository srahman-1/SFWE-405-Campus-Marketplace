package edu.sfwe405.campusmarketplace.controller;

import edu.sfwe405.campusmarketplace.dto.RegisterRequest;
import edu.sfwe405.campusmarketplace.model.Product;
import edu.sfwe405.campusmarketplace.model.UserAccount;
import edu.sfwe405.campusmarketplace.repository.ProductRepository;
import edu.sfwe405.campusmarketplace.repository.UserRepository;
import edu.sfwe405.campusmarketplace.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/ui")
public class UIController {

    private final UserRepository userRepo;
    private final ProductRepository productRepo;
    private final UserService userService;

    public UIController(UserRepository userRepo, ProductRepository productRepo, UserService userService) {
        this.userRepo = userRepo;
        this.productRepo = productRepo;
        this.userService = userService;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", userRepo.findAll());
        model.addAttribute("user", new UserAccount());
        return "users";
    }

    @PostMapping("/users")
    public String saveUser(
        @RequestParam String email,
        @RequestParam String password
    ) {
        userService.createUser(new RegisterRequest(email, password));
        return "redirect:/ui/users";
    }

    @GetMapping("/products")
    public String products(Model model) {
        model.addAttribute("products", productRepo.findAll());
        model.addAttribute("product", new Product());
        return "products";
    }

    @PostMapping("/products")
    public String saveProduct(Product product, Authentication authentication) {
        UserAccount owner = userService.getByEmailOrThrow(authentication.getName());
        product.setOwner(owner);
        productRepo.save(product);
        return "redirect:/ui/products";
    }
}
