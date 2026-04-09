package edu.sfwe405.campusmarketplace.controller;

import edu.sfwe405.campusmarketplace.model.Review;
import edu.sfwe405.campusmarketplace.repository.ReviewRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewRepository repo;

    public ReviewController(ReviewRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    public Review create(@RequestBody Review r) {
        return repo.save(r);
    }

    @GetMapping
    public List<Review> all() {
        return repo.findAll();
    }
}
