package edu.sfwe405.campusmarketplace.service;

import edu.sfwe405.campusmarketplace.dto.CartCheckoutRequest;
import edu.sfwe405.campusmarketplace.dto.CartCheckoutResponse;
import edu.sfwe405.campusmarketplace.dto.CartItemRequest;
import edu.sfwe405.campusmarketplace.dto.CartItemResponse;
import edu.sfwe405.campusmarketplace.dto.CartResponse;
import edu.sfwe405.campusmarketplace.model.CartItem;
import edu.sfwe405.campusmarketplace.model.Payment;
import edu.sfwe405.campusmarketplace.model.Product;
import edu.sfwe405.campusmarketplace.model.UserAccount;
import edu.sfwe405.campusmarketplace.repository.CartItemRepository;
import edu.sfwe405.campusmarketplace.repository.ProductRepository;
import edu.sfwe405.campusmarketplace.repository.UserRepository;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final PaymentService paymentService;
    private final InventoryService inventoryService;
    private final OrderService orderService;

    public CartService(
        UserRepository userRepository,
        ProductRepository productRepository,
        CartItemRepository cartItemRepository,
        PaymentService paymentService,
        InventoryService inventoryService,
        OrderService orderService
    ) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
        this.paymentService = paymentService;
        this.inventoryService = inventoryService;
        this.orderService = orderService;
    }

    public CartResponse addItem(Long buyerId, CartItemRequest request) {
        getBuyerOrThrow(buyerId);
        productRepository.findById(request.productId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        cartItemRepository.findByBuyerIdAndProductId(buyerId, request.productId())
            .ifPresentOrElse(
                item -> { item.setQuantity(item.getQuantity() + request.quantity()); cartItemRepository.save(item); },
                () -> cartItemRepository.save(new CartItem(buyerId, request.productId(), request.quantity()))
            );

        return viewCart(buyerId);
    }

    public CartResponse viewCart(Long buyerId) {
        List<CartItem> cartItems = cartItemRepository.findByBuyerId(buyerId);
        List<CartItemResponse> items = new ArrayList<>();
        double total = 0.0;

        for (CartItem cartItem : cartItems) {
            Product product = productRepository.findById(cartItem.getProductId()).orElse(null);
            if (product == null) continue;

            int quantity = cartItem.getQuantity();
            double lineTotal = product.getPrice() * quantity;
            total += lineTotal;
            items.add(new CartItemResponse(product.getId(), product.getName(), quantity, product.getPrice(), lineTotal));
        }

        return new CartResponse(buyerId, items, total);
    }

    public CartResponse removeItem(Long buyerId, Long productId) {
        cartItemRepository.deleteByBuyerIdAndProductId(buyerId, productId);
        return viewCart(buyerId);
    }

    public CartResponse updateItemQuantity(Long buyerId, Long productId, int quantity) {
        getBuyerOrThrow(buyerId);
        productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (quantity <= 0) {
            cartItemRepository.deleteByBuyerIdAndProductId(buyerId, productId);
            return viewCart(buyerId);
        }

        CartItem item = cartItemRepository.findByBuyerIdAndProductId(buyerId, productId)
            .orElse(new CartItem(buyerId, productId, quantity));
        item.setQuantity(quantity);
        cartItemRepository.save(item);

        return viewCart(buyerId);
    }

    @Transactional
    public CartCheckoutResponse checkout(Long buyerId, CartCheckoutRequest request) {
        UserAccount buyer = getBuyerOrThrow(buyerId);
        List<CartItem> cartItems = cartItemRepository.findByBuyerId(buyerId);

        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        double total = 0.0;
        List<Long> unavailable = new ArrayList<>();

        for (CartItem item : cartItems) {
            Product product = productRepository.findById(item.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

            if (!inventoryService.hasStock(product, item.getQuantity()))
                unavailable.add(product.getId());

            total += product.getPrice() * item.getQuantity();
        }

        if (!unavailable.isEmpty()) {
            return new CartCheckoutResponse(false, "One or more products are unavailable.", 0.0, List.of(), unavailable);
        }

        Payment payment = new Payment();
        payment.setAmount(total);
        payment.setMethod(request.paymentMethod());
        payment.setSuccess(!request.forcePaymentFailure());
        paymentService.create(payment);

        if (!payment.isSuccess()) {
            return new CartCheckoutResponse(false, "Payment failed.", total, List.of(), List.of());
        }

        List<Long> orderIds = new ArrayList<>();
        for (CartItem item : cartItems) {
            Product product = productRepository.findById(item.getProductId()).get();
            inventoryService.decrementStock(product, item.getQuantity());
            productRepository.save(product);
            orderIds.addAll(orderService.createPaidOrders(buyer, product, item.getQuantity()));
        }

        cartItemRepository.deleteByBuyerId(buyerId);
        return new CartCheckoutResponse(true, "Purchase successful.", total, orderIds, List.of());
    }

    private UserAccount getBuyerOrThrow(Long buyerId) {
        return userRepository.findById(buyerId)
            .orElseThrow(() -> new IllegalArgumentException("Buyer not found"));
    }
}
