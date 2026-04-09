package edu.sfwe405.campusmarketplace.repository;

import edu.sfwe405.campusmarketplace.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {}
