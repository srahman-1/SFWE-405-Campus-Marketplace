package edu.sfwe405.campusmarketplace.repository;

import edu.sfwe405.campusmarketplace.model.CartItem;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByBuyerId(Long buyerId);
    Optional<CartItem> findByBuyerIdAndProductId(Long buyerId, Long productId);
    void deleteByBuyerIdAndProductId(Long buyerId, Long productId);
    void deleteByBuyerId(Long buyerId);
}