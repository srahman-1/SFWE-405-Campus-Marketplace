package edu.sfwe405.campusmarketplace.service;

import edu.sfwe405.campusmarketplace.model.OrderItem;
import edu.sfwe405.campusmarketplace.repository.OrderItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemService {

    private final OrderItemRepository repo;

    public OrderItemService(OrderItemRepository repo) {
        this.repo = repo;
    }

    public OrderItem create(OrderItem i) {
        return repo.save(i);
    }

    public List<OrderItem> getAll() {
        return repo.findAll();
    }
}
