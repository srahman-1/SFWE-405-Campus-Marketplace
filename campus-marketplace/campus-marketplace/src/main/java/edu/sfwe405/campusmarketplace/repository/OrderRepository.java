package edu.sfwe405.campusmarketplace.repository;

import edu.sfwe405.campusmarketplace.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
//Order repository for marketplace system (extends jpa repository).
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByBuyer_IdAndPaidTrueOrderByCreatedAtDesc(Long buyerId);
}
