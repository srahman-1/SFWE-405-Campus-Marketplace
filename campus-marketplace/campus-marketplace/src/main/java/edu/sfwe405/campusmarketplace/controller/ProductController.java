package edu.sfwe405.campusmarketplace.controller;

import edu.sfwe405.campusmarketplace.dto.ProductDTO;
import edu.sfwe405.campusmarketplace.model.Product;
import edu.sfwe405.campusmarketplace.model.UserAccount;
import edu.sfwe405.campusmarketplace.service.ProductService;
import edu.sfwe405.campusmarketplace.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final UserService userService;

    public ProductController(ProductService productService, UserService userService) {
        this.productService = productService;
        this.userService = userService;
    }

    @PostMapping
    public Product createProduct(Authentication authentication, @Valid @RequestBody ProductDTO product) {
        UserAccount owner = userService.getByEmailOrThrow(authentication.getName());
        return productService.createProduct(owner, product);
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/me")
    public List<Product> getMyProducts(Authentication authentication) {
        UserAccount owner = userService.getByEmailOrThrow(authentication.getName());
        return productService.getMyProducts(owner.getId());
    }

    @PutMapping("/{id}")
    public Product updateProduct(
        Authentication authentication,
        @PathVariable Long id,
        @Valid @RequestBody ProductDTO product
    ) {
        UserAccount owner = userService.getByEmailOrThrow(authentication.getName());
        return productService.updateProduct(owner, id, product);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(Authentication authentication, @PathVariable Long id) {
        UserAccount owner = userService.getByEmailOrThrow(authentication.getName());
        productService.deleteProduct(owner, id);
    }
}
