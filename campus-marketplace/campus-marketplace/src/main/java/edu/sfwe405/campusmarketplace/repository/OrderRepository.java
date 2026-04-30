package edu.sfwe405.campusmarketplace.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.sfwe405.campusmarketplace.model.Order;

// Order repository for marketplace system.
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findDistinctByBuyer_IdOrProduct_Owner_IdOrderByCreatedAtDesc(Long buyerId, Long ownerId);

    Optional<Order> findByIdAndBuyer_EmailAndProduct_IdAndPaidTrue(
            Long orderId,
            String buyerEmail,
            Long productId
    );
}