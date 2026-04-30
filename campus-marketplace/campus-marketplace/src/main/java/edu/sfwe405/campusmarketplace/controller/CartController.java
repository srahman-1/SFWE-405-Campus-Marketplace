package edu.sfwe405.campusmarketplace.controller;

import edu.sfwe405.campusmarketplace.dto.CartCheckoutRequest;
import edu.sfwe405.campusmarketplace.dto.CartCheckoutResponse;
import edu.sfwe405.campusmarketplace.dto.CartItemRequest;
import edu.sfwe405.campusmarketplace.dto.CartResponse;
import edu.sfwe405.campusmarketplace.service.CartService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/{buyerId}/items")
    public CartResponse addItem(
        @PathVariable Long buyerId,
        @Valid @RequestBody CartItemRequest request
    ) {
        return cartService.addItem(buyerId, request);
    }

    @GetMapping("/{buyerId}")
    public CartResponse viewCart(@PathVariable Long buyerId) {
        return cartService.viewCart(buyerId);
    }

    @DeleteMapping("/{buyerId}/items/{productId}")
    public CartResponse removeItem(@PathVariable Long buyerId, @PathVariable Long productId) {
        return cartService.removeItem(buyerId, productId);
    }

    @PutMapping("/{buyerId}/items/{productId}")
    public CartResponse updateItemQuantity(
        @PathVariable Long buyerId,
        @PathVariable Long productId,
        @RequestParam int quantity
    ) {
        return cartService.updateItemQuantity(buyerId, productId, quantity);
    }

    @PostMapping("/{buyerId}/checkout")
    public CartCheckoutResponse checkout(
        @PathVariable Long buyerId,
        @Valid @RequestBody CartCheckoutRequest request
    ) {
        return cartService.checkout(buyerId, request);
    }
}
