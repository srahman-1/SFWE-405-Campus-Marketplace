package edu.sfwe405.campusmarketplace.service;

import edu.sfwe405.campusmarketplace.dto.ProductDTO;
import edu.sfwe405.campusmarketplace.model.Product;
import edu.sfwe405.campusmarketplace.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product createProduct(ProductDTO request) {
        Product product = new Product();
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock() == null ? 1 : request.stock());

        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
}
