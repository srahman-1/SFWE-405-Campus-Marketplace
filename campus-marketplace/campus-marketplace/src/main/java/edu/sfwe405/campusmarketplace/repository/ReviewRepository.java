package edu.sfwe405.campusmarketplace.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.sfwe405.campusmarketplace.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByProduct_IdOrderByCreatedAtDesc(Long productId);

    boolean existsByOrder_IdAndUser_Id(Long orderId, Long userId);
}