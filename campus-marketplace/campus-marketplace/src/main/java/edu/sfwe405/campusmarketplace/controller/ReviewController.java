package edu.sfwe405.campusmarketplace.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.sfwe405.campusmarketplace.dto.ReviewRequest;
import edu.sfwe405.campusmarketplace.dto.ReviewResponse;
import edu.sfwe405.campusmarketplace.service.ReviewService;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<?> createReview(
            Authentication authentication,
            @RequestBody ReviewRequest request) {

        try {
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.status(401).body(Map.of(
                        "message", "You must be logged in to submit a review."
                ));
            }

            ReviewResponse response = reviewService.createReview(authentication.getName(), request);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException error) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", error.getMessage()
            ));
        } catch (Exception error) {
            error.printStackTrace();

            return ResponseEntity.internalServerError().body(Map.of(
                    "message", "Database error. Review was not saved."
            ));
        }
    }

    @GetMapping("/product/{productId}")
    public List<ReviewResponse> getReviewsForProduct(@PathVariable Long productId) {
        return reviewService.getReviewsForProduct(productId);
    }

    @GetMapping
    public List<ReviewResponse> all() {
        return reviewService.getAllReviews();
    }
}