package edu.sfwe405.campusmarketplace.repository;

import edu.sfwe405.campusmarketplace.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {}
