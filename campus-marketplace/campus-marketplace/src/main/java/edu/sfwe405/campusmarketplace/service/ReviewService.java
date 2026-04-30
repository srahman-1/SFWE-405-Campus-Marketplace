package edu.sfwe405.campusmarketplace.service;

import java.util.List;

import org.springframework.stereotype.Service;

import edu.sfwe405.campusmarketplace.dto.ReviewRequest;
import edu.sfwe405.campusmarketplace.dto.ReviewResponse;
import edu.sfwe405.campusmarketplace.model.Order;
import edu.sfwe405.campusmarketplace.model.Review;
import edu.sfwe405.campusmarketplace.repository.OrderRepository;
import edu.sfwe405.campusmarketplace.repository.ReviewRepository;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         OrderRepository orderRepository) {
        this.reviewRepository = reviewRepository;
        this.orderRepository = orderRepository;
    }

    public ReviewResponse createReview(String customerEmail, ReviewRequest request) {
        validateRequest(request);

        String cleanEmail = customerEmail.trim().toLowerCase();
        String cleanComment = request.comment() == null ? "" : request.comment().trim();

        Order order = orderRepository
                .findByIdAndBuyer_EmailAndProduct_IdAndPaidTrue(
                        request.orderId(),
                        cleanEmail,
                        request.productId()
                )
                .orElseThrow(() -> new IllegalArgumentException(
                        "You can only review items you’ve purchased."
                ));

        if (reviewRepository.existsByOrder_IdAndUser_Id(order.getId(), order.getBuyer().getId())) {
            throw new IllegalArgumentException("You already reviewed this purchase.");
        }

        Review review = new Review();
        review.setRating(request.rating());
        review.setComment(cleanComment);
        review.setOrder(order);
        review.setProduct(order.getProduct());
        review.setUser(order.getBuyer());

        Review savedReview = reviewRepository.save(review);

        return toResponse(savedReview);
    }

    public List<ReviewResponse> getReviewsForProduct(Long productId) {
        return reviewRepository.findByProduct_IdOrderByCreatedAtDesc(productId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ReviewResponse> getAllReviews() {
        return reviewRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private void validateRequest(ReviewRequest request) {
        if (request.productId() == null) {
            throw new IllegalArgumentException("Product ID is required.");
        }

        if (request.orderId() == null) {
            throw new IllegalArgumentException("Order ID is required.");
        }

        if (request.rating() == null || request.rating() < 1 || request.rating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }

        if (request.comment() != null && request.comment().length() > 500) {
            throw new IllegalArgumentException("Comment must be 500 characters or less.");
        }
    }

    private ReviewResponse toResponse(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getProduct().getId(),
                review.getOrder().getId(),
                review.getUser().getEmail(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        );
    }
}