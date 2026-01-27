package edu.sfwe405.campusmarketplace.controller;

import edu.sfwe405.campusmarketplace.model.Order;
import edu.sfwe405.campusmarketplace.repository.OrderRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderRepository orderRepository;

    public OrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // CREATE order
    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        return orderRepository.save(order);
    }

    // GET all orders
    @GetMapping
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // PAY order (mock payment)
    @PostMapping("/{id}/pay")
    public Order payOrder(@PathVariable Long id) {
        Order order = orderRepository.findById(id).orElseThrow();
        order.setPaid(true);
        return orderRepository.save(order);
    }
}
