package edu.sfwe405.campusmarketplace.controller;

import edu.sfwe405.campusmarketplace.dto.ProductDTO;
import edu.sfwe405.campusmarketplace.model.Product;
import edu.sfwe405.campusmarketplace.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public Product createProduct(@Valid @RequestBody ProductDTO product) {
        return productService.createProduct(product);
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }
}
