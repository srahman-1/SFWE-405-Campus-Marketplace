package edu.sfwe405.campusmarketplace.repository;

import edu.sfwe405.campusmarketplace.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
//Order repository for marketplace system (extends jpa repository).
public interface OrderRepository extends JpaRepository<Order, Long> {}
