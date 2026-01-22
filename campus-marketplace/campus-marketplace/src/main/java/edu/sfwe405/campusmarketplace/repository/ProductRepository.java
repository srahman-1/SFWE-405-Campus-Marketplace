package edu.sfwe405.campusmarketplace.repository;

import edu.sfwe405.campusmarketplace.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
