package edu.sfwe405.campusmarketplace.controller;

import edu.sfwe405.campusmarketplace.dto.OrderHistoryResponse;
import edu.sfwe405.campusmarketplace.model.Order;
import edu.sfwe405.campusmarketplace.model.UserAccount;

import edu.sfwe405.campusmarketplace.repository.OrderRepository;

import edu.sfwe405.campusmarketplace.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderRepository orderRepository;
    private final UserService userService;

    public OrderController(OrderRepository orderRepository, UserService userService) {
        this.orderRepository = orderRepository;
        this.userService = userService;
    }

    // GET SIGNED-IN USER ORDERS
    @GetMapping("/me")
    public List<OrderHistoryResponse> getMyOrders(Authentication authentication) {
        UserAccount user = userService.getByEmailOrThrow(authentication.getName());
        return orderRepository.findDistinctByBuyer_IdOrProduct_Owner_IdOrderByCreatedAtDesc(user.getId(), user.getId())
            .stream()
            .map(order -> toHistoryResponse(order, user))
            .toList();
    }

    private OrderHistoryResponse toHistoryResponse(Order order, UserAccount currentUser) {
        boolean isSale = order.getProduct() != null
            && order.getProduct().getOwner() != null
            && order.getProduct().getOwner().getId() != null
            && order.getProduct().getOwner().getId().equals(currentUser.getId());

        return new OrderHistoryResponse(
            order.getId(),
            isSale ? "SALE" : "PURCHASE",
            order.getBuyer() != null ? order.getBuyer().getId() : null,
            order.getBuyer() != null ? order.getBuyer().getEmail() : null,
            order.getProduct() != null ? order.getProduct().getId() : null,
            order.getProduct() != null ? order.getProduct().getName() : null,
            order.getProduct() != null ? order.getProduct().getPrice() : 0.0,
            Math.max(order.getQuantity(), 1),
            order.isPaid(),
            order.getCreatedAt()
        );
    }
}
