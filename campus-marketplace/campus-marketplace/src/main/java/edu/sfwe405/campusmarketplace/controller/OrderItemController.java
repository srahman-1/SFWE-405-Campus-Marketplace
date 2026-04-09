package edu.sfwe405.campusmarketplace.controller;

import edu.sfwe405.campusmarketplace.model.OrderItem;
import edu.sfwe405.campusmarketplace.repository.OrderItemRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order-items")
public class OrderItemController {

    private final OrderItemRepository repo;

    public OrderItemController(OrderItemRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    public OrderItem create(@RequestBody OrderItem oi) {
        return repo.save(oi);
    }

    @GetMapping
    public List<OrderItem> all() {
        return repo.findAll();
    }
}
