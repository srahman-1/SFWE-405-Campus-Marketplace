package edu.sfwe405.campusmarketplace.service;

import edu.sfwe405.campusmarketplace.dto.ProductDTO;
import edu.sfwe405.campusmarketplace.model.Product;
import edu.sfwe405.campusmarketplace.model.UserAccount;
import edu.sfwe405.campusmarketplace.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product createProduct(UserAccount owner, ProductDTO request) {
        Product product = new Product();
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock() == null ? 1 : request.stock());
        product.setOwner(owner);

        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getMyProducts(Long ownerId) {
        return productRepository.findByOwner_IdOrderByCreatedAtDesc(ownerId);
    }

    public Product updateProduct(UserAccount owner, Long productId, ProductDTO request) {
        Product product = getOwnedProductOrThrow(owner, productId);
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        if (request.stock() != null) {
            product.setStock(request.stock());
        }
        return productRepository.save(product);
    }

    public void deleteProduct(UserAccount owner, Long productId) {
        Product product = getOwnedProductOrThrow(owner, productId);
        productRepository.delete(product);
    }

    private Product getOwnedProductOrThrow(UserAccount owner, Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        Long ownerId = product.getOwnerId();
        if (ownerId == null || !ownerId.equals(owner.getId())) {
            throw new IllegalArgumentException("You can only manage your own listings");
        }

        return product;
    }
}
