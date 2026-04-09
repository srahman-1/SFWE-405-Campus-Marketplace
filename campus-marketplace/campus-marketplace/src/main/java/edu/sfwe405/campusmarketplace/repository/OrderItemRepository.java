package edu.sfwe405.campusmarketplace.repository;

import edu.sfwe405.campusmarketplace.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {}
