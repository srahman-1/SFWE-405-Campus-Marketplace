package edu.sfwe405.campusmarketplace.service;

import edu.sfwe405.campusmarketplace.model.Order;
import edu.sfwe405.campusmarketplace.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order placeOrder(Order order) {
        // business rule: order starts unpaid
        order.setPaid(false);
        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
