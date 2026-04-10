package edu.sfwe405.campusmarketplace.service;

import edu.sfwe405.campusmarketplace.model.Order;
import edu.sfwe405.campusmarketplace.model.Product;
import edu.sfwe405.campusmarketplace.model.UserAccount;
import edu.sfwe405.campusmarketplace.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Long> createPaidOrders(UserAccount buyer, Product product, int quantity) {
        List<Long> orderIds = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            Order order = new Order();
            order.setBuyer(buyer);
            order.setProduct(product);
            order.setPaid(true);
            orderIds.add(orderRepository.save(order).getId());
        }
        return orderIds;
    }
}
