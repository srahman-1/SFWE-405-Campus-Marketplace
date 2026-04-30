package edu.sfwe405.campusmarketplace.repository;

import edu.sfwe405.campusmarketplace.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByOwner_IdOrderByCreatedAtDesc(Long ownerId);
    List<Product> findByOwner_IdNotOrderByCreatedAtDesc(Long ownerId);
}
