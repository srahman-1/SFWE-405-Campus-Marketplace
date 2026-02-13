package edu.sfwe405.campusmarketplace.controller;

import edu.sfwe405.campusmarketplace.model.Order;
import edu.sfwe405.campusmarketplace.model.Product;
import edu.sfwe405.campusmarketplace.model.UserAccount;

import edu.sfwe405.campusmarketplace.repository.OrderRepository;
import edu.sfwe405.campusmarketplace.repository.ProductRepository;
import edu.sfwe405.campusmarketplace.repository.UserRepository;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public OrderController(OrderRepository orderRepository,
                           UserRepository userRepository,
                           ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    // CREATE ORDER
    @PostMapping
    public Order createOrder(@RequestBody Map<String, Long> body) {

        Long buyerId = body.get("buyerId");
        Long productId = body.get("productId");

        if (buyerId == null || productId == null) {
            throw new RuntimeException("buyerId or productId missing");
        }

        UserAccount buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Order order = new Order();
        order.setBuyer(buyer);
        order.setProduct(product);
        order.setPaid(false);

        return orderRepository.save(order);
    }

    // GET ORDERS
    @GetMapping
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // PAY ORDER
    @PostMapping("/{id}/pay")
    public Order payOrder(@PathVariable Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.isPaid()) {
            throw new RuntimeException("Order already paid");
        }

        order.setPaid(true);
        return orderRepository.save(order);
    }
}
